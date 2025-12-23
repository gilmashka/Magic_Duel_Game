package server.game;

import common.models.Card;


/*
класс, управляющий игровой логикой, обрабатывающий операции по подсчету игровых моментов
 */
public final class GameLogic {

    //поля
    public static class ResultsDTO{
        private int wizard1ChangeHp;
        private int wizard2ChangeHp;

        public ResultsDTO(int wizard1ChangeHp, int wizard2ChangeHp){
            this.wizard1ChangeHp = wizard1ChangeHp;
            this.wizard2ChangeHp = wizard2ChangeHp;
        }

        public int getWizard1ChangeHp() {
            return wizard1ChangeHp;
        }

        public int getWizard2ChangeHp() {
            return wizard2ChangeHp;
        }
    }


    private GameLogic(){}//приватный конструктор для запрета на создание экземпляров класса

    /*
    методы для подсчета игровых моментов
     */
    //метод для подсчета итогов одного раунда
    public static ResultsDTO roundResult(Card card1, Card card2){
        int wizard2ChangeHP = 0;
        int wizard1ChangeHP = 0;
        if((card1.getAttack_parameter() - card2.getDefence_parameter()) > 0){
            wizard2ChangeHP = (card1.getAttack_parameter() - card2.getDefence_parameter());//урон от первого по второму
        }
        if((card2.getAttack_parameter() - card1.getDefence_parameter()) > 0){
            wizard1ChangeHP = (card2.getAttack_parameter() - card1.getDefence_parameter());//урон от второго по первому
        }

        return new ResultsDTO(wizard1ChangeHP, wizard2ChangeHP);
    }
}
