    public class DateComparator  implements Comparator<Conversation> {


        @Override
        public int compare(Conversation o1, Conversation o2) {
            return o2.getLastMessageDate().compareTo(o1.getLastMessageDate());
        }

    }
