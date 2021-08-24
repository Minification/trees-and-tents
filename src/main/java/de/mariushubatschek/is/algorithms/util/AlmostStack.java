package de.mariushubatschek.is.algorithms.util;

import de.mariushubatschek.is.modeling.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AlmostStack {

    private List<VariableValue> list = new ArrayList<>();

    public int size() {
        return list.size();
    }

    public void push(VariableValue vv) {
        list.add(vv);
    }

    public VariableValue top() {
        return list.get(list.size() - 1);
    }

    public VariableValue pop() {
        return list.remove(list.size() - 1);
    }

    public void delete(VariableValue toDelete) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(toDelete)) {
                list.remove(i);
                break;
            }
        }
        //list.remove(toDelete);
    }

    public List<Variable> variables() {
        return list.stream().map(v -> v.variable).collect(Collectors.toList());
    }

    public List<VariableValue> toList() {
        return list;
    }

    public List<VariableValue> withVariable(Variable variable) {
        return list.stream().filter(v -> v.variable.equals(variable)).collect(Collectors.toList());
    }

    public VariableValue findFirstFromTopIn(Collection<VariableValue> in) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (in.contains(list.get(i))) {
                return list.get(i);
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<VariableValue> getUpTo(int k) {
        return list.subList(0, k+1);
    }
}
