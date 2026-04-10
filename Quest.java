package studentdatabase.task3.m1;

public class Quest {
    private final String questName;
    private final String reservationStatus;
    private final String reservedByType;
    private final String reservedByName;

    private Quest(Builder builder) {
        this.questName = builder.questName;
        this.reservationStatus = builder.reservationStatus;
        this.reservedByType = builder.reservedByType;
        this.reservedByName = builder.reservedByName;
    }

    public String getQuestName() {
        return questName;
    }
    public String getReservationStatus() {
        return reservationStatus;
    }
    public String getReservedByType() {
        return reservedByType;
    }
    public String getReservedByName() {
        return reservedByName;
    }

    public static class Builder {
        private String questName;
        private String reservationStatus;
        private String reservedByType;
        private String reservedByName;

        public Builder setQuestName(String questName) {
            this.questName = questName;
            return this;
        }

        public Builder setReservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
            return this;
        }

        public Builder setReservedByType(String reservedByType) {
            this.reservedByType = reservedByType;
            return this;
        }

        public Builder setReservedByName(String reservedByName) {
            this.reservedByName = reservedByName;
            return this;
        }

        public Quest build() {
            return new Quest(this);
        }
    }
}
