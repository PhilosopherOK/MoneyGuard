package ua.bank.moneyguard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tituls {
    HERALD(2000l,"HERALD"), GUARDIAN(3500l,"GUARDIAN"),
    CRUSADER(6000l,"CRUSADER"), ARCHON(10000l,"ARCHON"), LEGEND(15000l,"LEGEND"),
    ANCIENT(25000l,"ANCIENT"), DIVINE(50000l,"DIVINE"), IMMORTAL(150000l,"IMMORTAL");


    private Long limitForService;
    private String name;

    public Long getLimitForService() {
        return limitForService;
    }
}
