package com.fulmicoton.collodion.processors.numberparser;

public enum NumberInterpreter {

    ENGLISH {
        @Override
        public double read(String val) {
            return Double.valueOf(val.replace(",", ""));
        }
    },

    FRENCH {
        @Override
        public double read(String val) {
            return Double.valueOf(val.replace(".", "").replace(",", "."));
        }
    };

    public abstract double read(final String val);

}
