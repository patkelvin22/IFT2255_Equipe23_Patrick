package models;

import java.util.HashMap;
import java.util.Map;

public enum Category {
    TRAVAUX_ROUTIERS("Travaux routiers", "Trotttoirs - Construction"),
    TRAVAUX_DE_GAZ_OU_ÉLECTRICITÉ("Travaux de gaz ou électricité", "S-2 Infrastructure souterraine mineure ou équipement hors-sol - Réseaux électriques, télécommunications ou câbles des distributions"),
    CONSTRUCTION_OU_RENOVATION("Construction ou rénovation", "Construction/rénovation sans excavation", "Construction/rénovation avec excavation"),
    ENTRETIEN_PAYSAGER("Entretien paysager", "Aménagement paysager"),
    TRAVAUX_LIÉS_AUX_TRANSPORTS_EN_COMMUN("Travaux liés aux transports en commun", "Travaux liés aux transports en commun"),
    TRAVAUX_DE_SIGNALISATION_ET_ECLAIRAGE("Travaux de signalisation et éclairage", "Installation de signalisation"),
    TRAVAUX_SOUTERRAINS("Travaux souterrains", "S-4 Déblocage de conduits souterrains", "Forage/excavation exploratoire", "Égouts et aqueducs - Excavation", "S-3 Infrastructure souterraine majeure - Puits d'accès", "Égouts et aqueducs - Inspection et nettoyage"),
    TRAVAUX_RÉSIDENTIEL("Travaux résidentiel", "Construction résidentielle"),
    ENTRETIEN_URBAIN("Entretien urbain", "Inspection", "Entretien", "Autre"),
    ENTRETIEN_DES_RÉSEAUX_DE_TÉLÉCOMMUNICATION("Entretien des réseaux de télécommunication", "AS-2 Réseau aérosouterrain existant");

    private final String displayName;
    private final String[] apiCategories;
    private static final Map<String, Category> apiToAppCategoryMap = new HashMap<>();

    // Initialisation du mapping entre API et Category
    static {
        for (Category category : Category.values()) {
            for (String apiCategory : category.apiCategories) {
                apiToAppCategoryMap.put(apiCategory, category);
            }
        }
    }

    // Constructeur avec un nom affichable et les catégories API
    Category(String displayName, String... apiCategories) {
        this.displayName = displayName;
        this.apiCategories = apiCategories;
    }

    // Méthode pour obtenir la catégorie d'application avec le nom formaté
    public static String getAppCategory(String apiCategory) {
        Category category = apiToAppCategoryMap.get(apiCategory);
        return category != null ? category.getDisplayName() : "Autre";
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category getCategoryByDisplayName(String displayName) {
        for (Category category : Category.values()) {
            if (category.getDisplayName().equals(displayName)) {
                return category;
            }
        }
        return null;
    }
}
