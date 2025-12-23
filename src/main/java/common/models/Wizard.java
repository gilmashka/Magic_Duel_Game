package common.models;

import java.util.List;

/*
класс-модель объекта "Маг"
описывает объект "Маг"
и его возможные действия/состояния
 */
public class Wizard {
    /*
    поля/конструкторы/геттеры
     */

    //поля
    private final int id;
    private final String type;
    private List<Card> deck;
    private int hp;

    //конструктор
    public Wizard(int id, String type, List<Card> deck) {
        this.id = id;
        this.type = type;
        this.deck = deck;
        this.hp = 30;
    }

    //геттеры
    public List<Card> getDeck() {
        return deck;
    }

    public int getHp() {
        return hp;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    /*
    методы/состояния
     */

    //сыграть карту
    public void playCard(Card card){
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).equals(card)) {
                deck.remove(card);
                System.out.println("Карта " + card.getName() + " сыграна магом " + this.getType());
                return;
            }
        }
        throw new IllegalArgumentException("Карта " + card.getName() + " не найдена в колоде!");
    }

    //состояние "живости"
    public boolean isAlive(){
        return hp > 0;
    }

    //проверка колоды на пустоту
    public boolean deckIsNotEmpty(){
        return !deck.isEmpty();
    }

    //получение урона
    public void takeDamage(int damage){
        if(hp - damage > 0){
            hp = hp - damage;
        }else{
            hp = 0;
        }
    }

    //проверка, что карта есть в колоде
    public boolean hasCard(int cardId){
        for(Card card: deck){
            if(card.getId() == cardId){
                return true;
            }

        }
        return false;
    }
}
