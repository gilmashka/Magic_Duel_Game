package common.services;

import common.models.Card;

import java.util.*;

/*
класс-сервис для работы с картами
отвечает за создание колоды всех карт,
колоды конкретных магов,
получение карты из колоды,
проверку подлинности карты
 */
public class CardService {
    //полный список карт для сервера, сделал map для доступа к конкретной карте за o(1)
    private static final Map<Integer, Card> fullCardList = new HashMap<>();
    //получение списка всех карт для серверных проверок
    public static Map<Integer, Card> getFullCardList(){
        return Collections.unmodifiableMap(fullCardList);//возвращаем специальный немодифицированный список, чтобы никто не мог изменить список всех карт извне
    }

    //создание списка всех карт в момент инициализации класса
    static{
        fillOutFullCardList();
    }


    /*
    методы, вызывающие конструкторы класса
    card для последующего создания различных карт
     */
    private static void fillOutFullCardList(){
        //
        addCard(1, "Точечный импульс", 3,0);
        addCard(2, "Кинетическая волна", 5,0);
        //
        addCard(3, "Искажающий луч", 4,2);
        addCard(4, "Резонаторная сфера", 2,4);
        //
        addCard(5, "Призрачный барьер", 0,3);
        addCard(6, "Изолирующая оболочка", 0,5);
        //
        addCard(7, "Инфернальный луч", 10,0);
        addCard(8, "Магматический щит", 8,2);
        //
        addCard(9, "Абсолютный нуль", 4,6);
        addCard(10, "Пронизывающий холод", 5,5);
        //
        addCard(11, "Сталактитная завеса", 2,8);
        addCard(12, "Литосерный щит", 0,10);
    }

    private static void addCard(int id, String name, int attack_parameter, int defence_parameter) {
        fullCardList.put(id, new Card(id, name, attack_parameter, defence_parameter));
    }

    //метод для проверки "действительно ли отправленная карта существует?"
    public static boolean isInCardList(int key){
        return fullCardList.containsKey(key);
    }

    //метод для получения карты !!!только внутри этого класса, для остальных случаев пользоваться списком всех карт
    private static Card getCard(int id){
        return fullCardList.get(id);
    }

    /*
    колоды магов, тут уже время получения роль не сыграет,
    так как обращение к списку по элементу
    (скорее всего) осуществляться не будет
     */
    //пиромант
    public static List<Card> getPyromantDeck(){
        List<Card> pyromantCards = new ArrayList<>(getBaseCards());
        for(int i = 7; i<=8; i++){
            pyromantCards.add(fullCardList.get(i));
        }
        return pyromantCards;
    }

    //криомант
    public static List<Card> getCryomantDeck(){
        List<Card> cryomantCards = new ArrayList<>(getBaseCards());
        for(int i = 9; i<=10; i++){
            cryomantCards.add(getCard(i));
        }
        return cryomantCards;
    }

    //геомант
    public static List<Card> getGeomantDeck(){
        List<Card> geomantCards = new ArrayList<>(getBaseCards());
        for(int i = 11; i<=12; i++){
            geomantCards.add(getCard(i));
        }
        return geomantCards;
    }

    //получение базовых карт
    private static List<Card> getBaseCards(){
        List<Card> baseCards = new ArrayList<>();
        for(int i = 1; i<7; i++){
            baseCards.add(getCard(i));
        }
        return baseCards;
    }

}
