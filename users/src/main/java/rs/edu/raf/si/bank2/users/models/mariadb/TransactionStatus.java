package rs.edu.raf.si.bank2.users.models.mariadb;

public enum TransactionStatus {
    /**
     * Pocetno stanje je waiting. Iz stanja waiting moze da se prebaci ili u denied ili u in progress. Iz in progress moze da se prebaci u complete.
     * */
    WAITING,
    DENIED,
    IN_PROGRESS,
    COMPLETE
}
