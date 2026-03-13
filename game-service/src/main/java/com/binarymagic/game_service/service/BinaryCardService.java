package com.binarymagic.game_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BinaryCardService {

    private static final List<List<Integer>> CARDS = buildCards();

    public List<List<Integer>> getCards() {
        return CARDS;
    }

    private static List<List<Integer>> buildCards() {
        List<List<Integer>> cards = new ArrayList<>();
        for (int bit = 0; bit < 6; bit++) {
            List<Integer> card = new ArrayList<>();
            for (int n = 1; n <= 63; n++) {
                if ((n & (1 << bit)) != 0) card.add(n);
            }
            cards.add(Collections.unmodifiableList(card));
        }
        return Collections.unmodifiableList(cards);
    }
}
