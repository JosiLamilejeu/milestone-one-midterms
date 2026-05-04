package studentdatabase.task3.m1;

public class Quest {
    private final String QUEST_NAME;
    private final String RESERVATION_STATUS;
    private final String RESERVED_BY_TYPE;
    private final String RESERVED_BY_NAME;

    private Quest(QuestBuilder builder) {
        this.QUEST_NAME = builder.questName;
        this.RESERVATION_STATUS = builder.reservationStatus;
        this.RESERVED_BY_TYPE = builder.reservedByType;
        this.RESERVED_BY_NAME = builder.reservedByName;
    }

    public String getQuestName() {
        return QUEST_NAME;
    }
    public String getReservationStatus() {
        return RESERVATION_STATUS;
    }
    public String getReservedByType() {
        return RESERVED_BY_TYPE;
    }
    public String getReservedByName() {
        return RESERVED_BY_NAME;
    }

    public static class QuestBuilder {
        private String questName;
        private String reservationStatus;
        private String reservedByType;
        private String reservedByName;

        public QuestBuilder setQuestName(String questName) {
            this.questName = questName;
            return this;
        }

        public QuestBuilder setReservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
            return this;
        }

        public QuestBuilder setReservedByType(String reservedByType) {
            this.reservedByType = reservedByType;
            return this;
        }

        public QuestBuilder setReservedByName(String reservedByName) {
            this.reservedByName = reservedByName;
            return this;
        }

        public Quest build() {
            return new Quest(this);
        }
    }
}
