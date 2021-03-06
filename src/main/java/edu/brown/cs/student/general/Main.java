package edu.brown.cs.student.general;

import edu.brown.cs.student.database.APIException;
import edu.brown.cs.student.database.FieldParser;
import edu.brown.cs.student.database.RecipeDatabase;
import edu.brown.cs.student.food.NutrientInfo;
import edu.brown.cs.student.food.Recipe;
import edu.brown.cs.student.gui.Gui;
import edu.brown.cs.student.login.AccountException;
import edu.brown.cs.student.login.Accounts;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] inputArgs) {
    args = inputArgs;
  }

  private void run() {
    // initialize users maps
    try {
      NutrientInfo.createNutrientsList();
      RecipeDatabase.loadDatabase("data/recipeDatabase.sqlite3");
      Accounts.initializeMap();
      NutrientInfo.createNutrientsList();
    } catch (AccountException | FileNotFoundException | ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }

    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      Gui gui = new Gui();
      gui.runSparkServer((int) options.valueOf("port"));
    } else {
      try {
        RecipeDatabase.loadDatabase("data/recipeDatabase.sqlite3");
        NutrientInfo.createNutrientsList();
        Set<String> dietaryRestrictions = new HashSet<>();
        Map<String, String[]> paramsMap = new HashMap<>();
        paramsMap.put("x", new String[] {"x"});
        Recipe[] recipes = FieldParser.getRecipesFromQuery("pasta", dietaryRestrictions, paramsMap);
        for (Recipe recipe : recipes) {
          System.out.println(recipe.getUri());
        }
      } catch (IOException | InterruptedException | APIException | SQLException
              | ClassNotFoundException ie) {
        ie.printStackTrace();
      }
    }
  }
}
