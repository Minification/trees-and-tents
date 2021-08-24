package de.mariushubatschek.is.modeling;

import java.util.List;
import java.util.Objects;

public class Variable {

    private String name;

    private Domain domain;

    private Domain initialDomain;

    private double weight = 1;

    public Variable(String name, Domain domain) {
        this.name = name;
        this.domain = domain;
        this.initialDomain = domain.copy();
        //System.out.println("Creating variable: " + name);
    }

    public Domain getDomain() {
        return domain;
    }

    public Domain getInitialDomain() {
        return initialDomain;
    }

    public String getName() {
        return name;
    }

    public void replaceDomain(final Object[] values) {
        this.domain = new Domain(values);
        this.initialDomain = this.domain.copy();
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void changeWeightBy(final double delta) {
        this.weight += delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                '}';
    }
}
