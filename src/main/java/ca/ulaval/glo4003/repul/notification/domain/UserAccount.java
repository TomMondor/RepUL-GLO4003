package ca.ulaval.glo4003.repul.notification.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class UserAccount extends Account {
    private final Set<UniqueIdentifier> mealKits;
    public UserAccount(UniqueIdentifier accountId, Email email) {
        super(accountId, email);
        mealKits = new HashSet<>();
    }

    public void addMealKit(UniqueIdentifier mealkitId) {
        mealKits.add(mealkitId);
    }

    public List<UniqueIdentifier> getMealKitIds() {
        return mealKits.stream().toList();
    }
}
