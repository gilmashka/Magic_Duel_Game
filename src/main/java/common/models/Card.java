package common.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
класс-модель объекта "Карта"
описывает объект "Карта"
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    /*
    поля объекта, создавать что-то большее нежели это не вижу смысла,
    так как пока реализуем только обычные карты без эффектов,
    то создавать отдельный класс под каждую карты не вижу смысла,
    лучше потом добавить менеджер карт
     */
    private final int id;
    private final String name;
    private final int attack_parameter;
    private final int defence_parameter;

    @JsonCreator
    public Card(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("attack_parameter") int attack_parameter,
            @JsonProperty("defence_parameter") int defence_parameter) {
        this.id = id;
        this.name = name;
        this.attack_parameter = attack_parameter;
        this.defence_parameter = defence_parameter;
    }

    //геттеры
    public int getAttack_parameter() {
        return attack_parameter;
    }

    public int getDefence_parameter() {
        return defence_parameter;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //переопределение метода equals для возможности сыграть карту (Wizard.playCard)
    public boolean equals(Card card){
        if(this.id == card.id){
            return true;
        }
        else return false;
    }
}
