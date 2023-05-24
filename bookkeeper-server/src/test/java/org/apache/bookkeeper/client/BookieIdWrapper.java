package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;

public class BookieIdWrapper {
    private final BookieId bookieId;

    public BookieIdWrapper(BookieId bookieId) {
        this.bookieId = bookieId;
    }

    // Metodo getter per ottenere l'istanza di BookieId incapsulata

    public BookieId getBookieId() {
        return bookieId;
    }

    public String getStringBookieId(){
        return this.bookieId.getId();
    }


    // Altri metodi di supporto necessari
}

