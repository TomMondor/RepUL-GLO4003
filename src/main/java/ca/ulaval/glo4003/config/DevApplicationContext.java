package ca.ulaval.glo4003.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.commons.api.exception.mapper.CatchallExceptionMapper;
import ca.ulaval.glo4003.commons.api.exception.mapper.CommonExceptionMapper;
import ca.ulaval.glo4003.commons.api.exception.mapper.ConstraintViolationExceptionMapper;
import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.config.http.CORSResponseFilter;
import ca.ulaval.glo4003.identitymanagement.api.AuthResource;
import ca.ulaval.glo4003.identitymanagement.api.exception.IdentityManagementExceptionMapper;
import ca.ulaval.glo4003.identitymanagement.application.AuthFacade;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenDecoder;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;
import ca.ulaval.glo4003.identitymanagement.infrastructure.CryptPasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.infrastructure.InMemoryUserRepository;
import ca.ulaval.glo4003.identitymanagement.infrastructure.JWTTokenDecoder;
import ca.ulaval.glo4003.identitymanagement.infrastructure.JWTTokenGenerator;
import ca.ulaval.glo4003.identitymanagement.middleware.AuthGuard;
import ca.ulaval.glo4003.repul.api.HealthResource;
import ca.ulaval.glo4003.repul.api.account.AccountResource;
import ca.ulaval.glo4003.repul.api.catalog.CatalogResource;
import ca.ulaval.glo4003.repul.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.api.lunchbox.LunchboxResource;
import ca.ulaval.glo4003.repul.api.order.OrderResource;
import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.application.account.AccountService;
import ca.ulaval.glo4003.repul.application.catalog.CatalogService;
import ca.ulaval.glo4003.repul.application.lunchbox.LunchboxService;
import ca.ulaval.glo4003.repul.application.order.OrderService;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.IngredientInformation;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.infrastructure.EmulatedPaymentHandler;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DevApplicationContext implements ApplicationContext {
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";
    private static final String SEMESTERS_FILE_PATH = "src/main/resources/semesters-232425.json";
    private static final String SEMESTER_CODE_FIELD_NAME_IN_JSON = "semester_code";
    private static final String START_DATE_FIELD_NAME_IN_JSON = "start_date";
    private static final String END_DATE_FIELD_NAME_IN_JSON = "end_date";
    private static final String INGREDIENTS_FILE_PATH = "src/main/resources/ingredients.csv";
    private static final String STANDARD_LUNCHBOX_FILE_PATH = "src/main/resources/standard-meal-box.json";
    private static final Double STANDARD_PRICE = 75.0;
    private static final String RECIPES_FIELD_NAME_IN_JSON = "recipes";
    private static final String RECIPE_NAME_FIELD_NAME_IN_JSON = "name";
    private static final String RECIPE_CALORIES_FIELD_NAME_IN_JSON = "calories";
    private static final String RECIPE_INGREDIENTS_FIELD_NAME_IN_JSON = "ingredients";
    private static final String RECIPE_INGREDIENT_NAME_FIELD_NAME_IN_JSON = "ingredient";
    private static final String RECIPE_INGREDIENT_QUANTITY_FIELD_NAME_IN_JSON = "quantity";

    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);

    protected RepULRepository repULRepository;
    protected UserRepository userRepository;

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }

    private static AuthResource createAuthResource(AuthService authService) {
        LOGGER.info("Setup auth resource");
        return new AuthResource(authService);
    }

    private static AccountResource createAccountResource(RepULRepository repULRepository, AuthFacade authFacade) {
        LOGGER.info("Setup account resource");
        AccountFactory accountFactory = new AccountFactory();
        AccountService accountService = new AccountService(repULRepository, accountFactory, authFacade);
        return new AccountResource(accountService);
    }

    private static SubscriptionResource createSubscriptionResource(RepULRepository repULRepository) {
        LOGGER.info("Setup subscription resource");
        PaymentHandler paymentHandler = new EmulatedPaymentHandler();

        SubscriptionService subscriptionService = new SubscriptionService(repULRepository, paymentHandler);

        return new SubscriptionResource(subscriptionService);
    }

    private static OrderResource createOrderResource(RepULRepository repULRepository) {
        LOGGER.info("Setup order resource");

        OrderService orderService = new OrderService(repULRepository);

        return new OrderResource(orderService);
    }

    private static CatalogResource createCatalogResource(RepULRepository repULRepository) {
        LOGGER.info("Setup catalog resource");
        CatalogService catalogService = new CatalogService(repULRepository);
        return new CatalogResource(catalogService);
    }

    private static LunchboxResource createLunchboxResource(RepULRepository repULRepository) {
        LOGGER.info("Setup lunchbox resource");
        LunchboxService lunchboxService = new LunchboxService(repULRepository);
        return new LunchboxResource(lunchboxService);
    }

    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        UniqueIdentifierFactory uniqueIdentifierFactory = new UniqueIdentifierFactory();
        PasswordEncoder passwordEncoder = new CryptPasswordEncoder();
        TokenGenerator tokenGenerator = new JWTTokenGenerator();
        TokenDecoder tokenDecoder = new JWTTokenDecoder();

        UserFactory userFactory = new UserFactory(passwordEncoder);

        UserRepository userRepository = this.userRepository = new InMemoryUserRepository();

        AuthService authService = new AuthService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator);

        RepULRepository repULRepository = this.repULRepository = new InMemoryRepULRepository();

        initializeRepUL(repULRepository, uniqueIdentifierFactory);

        // Create user for cook
        User cookUser = createCookUser();
        userRepository.saveOrUpdate(cookUser);

        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();
        AuthResource authResource = createAuthResource(authService);
        SubscriptionResource subscriptionResource = createSubscriptionResource(repULRepository);
        OrderResource orderResource = createOrderResource(repULRepository);
        CatalogResource catalogResource = createCatalogResource(repULRepository);
        AccountResource accountResource = createAccountResource(repULRepository, authService);
        LunchboxResource lunchboxResource = createLunchboxResource(repULRepository);

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(authResource).to(AuthResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
                bind(orderResource).to(OrderResource.class);
                bind(accountResource).to(AccountResource.class);
                bind(catalogResource).to(CatalogResource.class);
                bind(lunchboxResource).to(LunchboxResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(new AuthGuard(userRepository, tokenDecoder)).register(new CORSResponseFilter()).register(new IdentityManagementExceptionMapper())
            .register(new CatchallExceptionMapper()).register(new RepULExceptionMapper()).register(new CommonExceptionMapper())
            .register(new ConstraintViolationExceptionMapper());
    }

    public void initializeRepUL(RepULRepository repULRepository, UniqueIdentifierFactory uniqueIdentifierFactory) {
        Catalog catalog = createCatalog();
        SubscriptionFactory subscriptionFactory = new SubscriptionFactory(uniqueIdentifierFactory);
        RepUL repUL = new RepUL(catalog, subscriptionFactory);
        repULRepository.saveOrUpdate(repUL);
    }

    private User createCookUser() {
        UserFactory userFactory = new UserFactory(new CryptPasswordEncoder());

        Email email = new Email("cook@ulaval.ca");
        Password password = new Password("cook");
        Role role = Role.COOK;
        UniqueIdentifier uid = new UniqueIdentifierFactory().generate();

        return userFactory.createUser(uid, email, role, password);
    }

    private Catalog createCatalog() {
        LOGGER.info("Setup catalog");
        List<PickupLocation> pickupLocations = parsePickupLocations();
        List<Semester> semesters = parseSemesters();
        List<IngredientInformation> ingredientInformations = parseIngredientInformation();
        Lunchbox standardLunchbox = parseStandardLunchbox();
        Map<LunchboxType, Amount> lunchboxTypeAmountMap = createLunchboxPriceMapping();
        return new Catalog(pickupLocations, semesters, ingredientInformations, standardLunchbox, lunchboxTypeAmountMap);
    }

    private List<PickupLocation> parsePickupLocations() {
        List<Map<String, Object>> listOfLocationMaps = getListOfMapsFromJsonFilePath(CAMPUS_STATIONS_LOCATION_FILE_PATH);
        return listOfLocationMaps.stream().map(
            map -> new PickupLocation(new LocationId((String) map.get(LOCATION_FIELD_NAME_IN_JSON)), (String) map.get(NAME_FIELD_NAME_IN_JSON),
                (int) map.get(CAPACITY_FIELD_NAME_IN_JSON))).toList();
    }

    private List<Semester> parseSemesters() {
        List<Map<String, Object>> listOfSemesterMaps = getListOfMapsFromJsonFilePath(SEMESTERS_FILE_PATH);
        return listOfSemesterMaps.stream().map(map -> new Semester(new SemesterCode((String) map.get(SEMESTER_CODE_FIELD_NAME_IN_JSON)),
            parseDate((String) map.get(START_DATE_FIELD_NAME_IN_JSON)), parseDate((String) map.get(END_DATE_FIELD_NAME_IN_JSON)))).toList();
    }

    private List<IngredientInformation> parseIngredientInformation() {
        List<IngredientInformation> ingredients = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INGREDIENTS_FILE_PATH))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                ingredients.add(new IngredientInformation(values[0], new Amount(Double.parseDouble(values[1]))));
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading " + INGREDIENTS_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + INGREDIENTS_FILE_PATH);
        }
        return ingredients;
    }

    private Lunchbox parseStandardLunchbox() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object listOfRecipesMaps = objectMapper.readValue(new File(STANDARD_LUNCHBOX_FILE_PATH), new TypeReference<Map<String, Object>>() {
            }).get(RECIPES_FIELD_NAME_IN_JSON);
            return new Lunchbox(getRecipesFromJsonList(listOfRecipesMaps));
        } catch (IOException e) {
            LOGGER.error("Error while reading " + STANDARD_LUNCHBOX_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + STANDARD_LUNCHBOX_FILE_PATH);
        }
    }

    private Map<LunchboxType, Amount> createLunchboxPriceMapping() {
        Map<LunchboxType, Amount> lunchboxTypeAmountMap = new HashMap<>();
        lunchboxTypeAmountMap.put(LunchboxType.STANDARD, new Amount(STANDARD_PRICE));

        return lunchboxTypeAmountMap;
    }

    private List<Recipe> getRecipesFromJsonList(Object listOfRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map<String, Object> map : (List<Map<String, Object>>) listOfRecipes) {
            recipes.add(getRecipeFromMap(map));
        }
        return recipes;
    }

    private Recipe getRecipeFromMap(Map<String, Object> map) {
        String recipeName = (String) map.get(RECIPE_NAME_FIELD_NAME_IN_JSON);
        int recipeCalories = getCaloriesFromString((String) map.get(RECIPE_CALORIES_FIELD_NAME_IN_JSON));
        List<Map<String, Object>> listOfIngredientsMaps = (List<Map<String, Object>>) map.get(RECIPE_INGREDIENTS_FIELD_NAME_IN_JSON);
        List<Ingredient> ingredients = new ArrayList<>();
        for (Map<String, Object> ingredientMap : listOfIngredientsMaps) {
            String ingredientName = (String) ingredientMap.get(RECIPE_INGREDIENT_NAME_FIELD_NAME_IN_JSON);
            Quantity ingredientQuantity = getIngredientQuantityFromString((String) ingredientMap.get(RECIPE_INGREDIENT_QUANTITY_FIELD_NAME_IN_JSON));
            ingredients.add(new Ingredient(ingredientName, ingredientQuantity));
        }
        return new Recipe(recipeName, recipeCalories, ingredients);
    }

    private int getCaloriesFromString(String calories) {
        try {
            return Integer.parseInt(calories);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing calories", e);
            throw new RuntimeException("Error while parsing calories");
        }
    }

    private Quantity getIngredientQuantityFromString(String quantity) {
        String[] quantitySplit = splitQuantity(quantity);
        try {
            return new Quantity(Double.parseDouble(quantitySplit[0]), quantitySplit[1]);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing quantity", e);
            throw new RuntimeException("Error while parsing quantity");
        }
    }

    private String[] splitQuantity(String quantity) {
        Pattern pattern = Pattern.compile("([0-9]+)\\s*([^0-9]*)$");
        Matcher matcher = pattern.matcher(quantity.trim());

        String[] result = new String[2];
        if (matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        } else {
            result[0] = quantity.trim();
            result[1] = "";
        }

        return result;
    }

    private List<Map<String, Object>> getListOfMapsFromJsonFilePath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> listOfMaps = objectMapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {
            });
            return listOfMaps;
        } catch (IOException e) {
            LOGGER.error("Error while reading " + filePath, e);
            throw new RuntimeException("Error while reading " + filePath);
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            LOGGER.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date");
        }
    }
}
