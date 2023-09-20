package ca.ulaval.glo4003.repul.domain;

public interface RepULRepository {
    void saveOrUpdate(RepUL repUL);

    RepUL get();
}
