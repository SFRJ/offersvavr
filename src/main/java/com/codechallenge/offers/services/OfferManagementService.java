package com.codechallenge.offers.services;

import com.codechallenge.offers.domain.Offer;
import com.codechallenge.offers.repositories.OffersRepository;
import io.vavr.Value;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

import static com.codechallenge.offers.domain.OfferStatus.ACTIVE;
import static com.codechallenge.offers.domain.OfferStatus.CANCELLED;

@Service
@RequiredArgsConstructor
public class OfferManagementService {

    private final OffersRepository offersRepository;

    public Try<UUID> createOffer(String description, Double price, LocalDate expiration) {

        return offersRepository.createOffer(Offer.builder()
                .identifier(UUID.randomUUID())
                .descriptions(description)
                .price(price)
                .expirationDate(expiration)
                .offerStatus(ACTIVE)
                .build());
    }

    public Try<Offer> getOffer(UUID offerId) {

        return offersRepository.readOffer(offerId)
                .flatMap(Value::toTry)
                .map(offer -> {
                    if(LocalDate.now().isAfter(offer.getExpirationDate())) {
                        offer.setOfferStatus(CANCELLED);
                        cancelOffer(offer.getIdentifier());
                    }
                        return offer;
                });
    }

    public Try<Offer> cancelOffer(UUID offerId) {

        return offersRepository.cancelOffer(offerId).flatMap(Value::toTry);
    }

}
