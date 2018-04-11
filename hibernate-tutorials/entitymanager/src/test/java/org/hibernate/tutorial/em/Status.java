package org.hibernate.tutorial.em;

public enum Status {

    APPROVED(1L), DENIED(2L);

    private final Long id;

    Status(Long id) {
        this.id = id;
    }



}
