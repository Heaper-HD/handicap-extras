package com.heaper;

import com.handicap.Handicap;

public class HandicapBehaviour {
    private final String id;
    private final Handicap handicap;

    protected HandicapBehaviour(String id) {
        this.id = id;
        this.handicap = Handicap.byId(id);
        if (this.handicap == null) {
            throw new IllegalStateException("No Handicap enum constant with id '" + id
                    + "'. Is it declared in HandicapsMixin with the same id?");
        }
    }

    public final String id() {
        return id;
    }

    public final Handicap handicap() {
        return handicap;
    }
}
