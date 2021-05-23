package com.dashuai009.todo.beans

enum class State(public val intValue: Int) {
    TODO(0), DONE(1);

    companion object {///？？？？？？？？？？
        fun from(intValue: Int): State {
            for (state in State.values()) {
                if (state.intValue == intValue) {
                    return state
                }
            }
            return TODO // default
        }
    }

}
