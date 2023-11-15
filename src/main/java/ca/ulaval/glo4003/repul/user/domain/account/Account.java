package ca.ulaval.glo4003.repul.user.domain.account;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class Account {
    private final UniqueIdentifier accountId;
    private final IDUL idul;
    private final Name name;
    private final Birthdate birthdate;
    private final Gender gender;
    private final Email email;
    private Optional<UserCardNumber> cardNumber = Optional.empty();

    public Account(UniqueIdentifier accountId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        this.accountId = accountId;
        this.idul = idul;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }

    public UniqueIdentifier getAccountId() {
        return accountId;
    }

    public IDUL getIdul() {
        return idul;
    }

    public Name getName() {
        return name;
    }

    public Birthdate getBirthdate() {
        return birthdate;
    }

    public Gender getGender() {
        return gender;
    }

    public Email getEmail() {
        return email;
    }

    public Optional<UserCardNumber> getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(UserCardNumber cardNumber) {
        this.cardNumber = Optional.of(cardNumber);
    }
}
