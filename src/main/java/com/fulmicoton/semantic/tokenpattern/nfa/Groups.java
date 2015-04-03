package com.fulmicoton.semantic.tokenpattern.nfa;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Groups implements Iterable<Groups> {

    public static enum OP {
        OPEN,
        CLOSE
    }
    final OP op;
    final int groupId;
    final int offset;
    final Groups next;

    public Groups(final OP op,
                  final int groupId,
                  final int offset,
                  final Groups next) {
        this.op = op;
        this.groupId = groupId;
        this.offset = offset;
        this.next = next;
    }

    public static Groups openGroup(Groups groups, int groupId, int offset) {
        return new Groups(OP.OPEN, groupId, offset, groups);
    }


    public static Groups closeGroup(Groups groups, int groupId, int offset) {
        return new Groups(OP.CLOSE, groupId, offset, groups);
    }



    @Override
    public Iterator<Groups> iterator() {
        List<Groups> groups = new ArrayList<>();
        for (Groups groupCur=this; groupCur!=null; groupCur=groupCur.next) {
            groups.add(groupCur);
        }
        Collections.reverse(groups);
        return groups.iterator();
    }
}
