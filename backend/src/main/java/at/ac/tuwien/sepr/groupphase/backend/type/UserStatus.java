package at.ac.tuwien.sepr.groupphase.backend.type;

/**
 * Status of the user account
 * UNLOCKED ... account is not locked
 * LOCKED ... account is locked
 * UNVERIFIED ... email of the account was not verified
 */
public enum UserStatus {
    UNLOCKED,
    LOCKED,
    UNVERIFIED
}
