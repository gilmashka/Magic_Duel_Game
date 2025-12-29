package common.services;

import common.models.Wizard;

/*
класс для создания магов каждого типа
 */
public class WizardFactory {

    private static int id = 1;


    /*
    методы по созданию магов
     */
    //общий метод по созданию мага, будет вызываться извне, в зависимости от переданного значения делегирует создание мага определенному методу
    public Wizard createWizard(String wizardType) throws Exception {
        if (wizardType == null) {
            throw new Exception("Тип мага не указан");
        }

        String type = wizardType.toUpperCase();

        return switch (type) {
            case "PYROMANCER", "PYROMANT" -> createPyromant();
            case "CRYOMANCER", "CRYOMANT" -> createCryomant();
            case "GEOMANCER", "GEOMANT" -> createGeomant();
            default -> throw new Exception("некорректный тип мага: " + wizardType);
        };
    }

    private static Wizard createPyromant(){
        return new Wizard(id++, "Пиромант", CardService.getPyromantDeck());
    }

   private static Wizard createCryomant(){
        return new Wizard(id++, "Криомант", CardService.getCryomantDeck());
    }

    private static Wizard createGeomant(){
        return new Wizard(id++, "Геомант", CardService.getGeomantDeck());
    }
}
