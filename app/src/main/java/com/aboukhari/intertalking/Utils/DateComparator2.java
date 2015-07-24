package com.aboukhari.intertalking.Utils;

import com.aboukhari.intertalking.model.Conversation;

import java.util.Comparator;

/**
 * Created by aboukhari on 24/07/2015.
 */

    public class DateComparator2  implements Comparator<Conversation> {


        @Override
        public int compare(Conversation o1, Conversation o2) {
            return o2.getLastMessageDate().compareTo(o1.getLastMessageDate());
        }

    }

