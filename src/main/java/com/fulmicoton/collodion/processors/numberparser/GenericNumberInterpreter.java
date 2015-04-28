package com.fulmicoton.collodion.processors.numberparser;

public abstract class GenericNumberInterpreter implements NumberInterpreter {

    final NumberInterpreter withMultiplier(final double multiplier) {
        final NumberInterpreter original = this;
        return new NumberInterpreter() {
            @Override
            public double read(String val) {
                return original.read(val) * multiplier;
            }
        };
    }

    final static GenericNumberInterpreter ENGLISH = new GenericNumberInterpreter() {
        @Override
        public String transform(String val) {
            return val.replaceAll("[,a-zA-Z]", "");
        }
    };

    final static GenericNumberInterpreter FRENCH = new GenericNumberInterpreter() {
        @Override
        public String transform(String val) {
            return val.replaceAll("[\\.a-zA-Z]", "").replace(",", ".");
        }
    };


    public abstract String transform(final String val);

    public double read(final String val) {
        final String transformed = this.transform(val);
        return Double.valueOf(transformed);
    }

}
