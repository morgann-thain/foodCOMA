package edu.brown.cs.student.recommendation;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.student.database.APIException;
import edu.brown.cs.student.database.FieldParser;
import edu.brown.cs.student.food.NutrientInfo;
import edu.brown.cs.student.food.Recipe;
import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.kdtree.KDTreeException;
import edu.brown.cs.student.login.User;

/**
 * This class contains the code to compile recipe recommendations based on user input and history.
 */
public class Recommender {
  private KDTree<RecipeNode> recipeTree;
  private static final int REC_QUANTITY = 100;
  private final int dim = NutrientInfo.getNutrientCodes().size();
  private static final double USER_PREF_WEIGHT = 6.;
  private static final double MAIN_NUT_WEIGHT = 3.;
  private static final double SEC_NUT_WEIGHT = 1.;
  private final User user;
  private static List<Double> distances;

  /**
   * constructor; should be called on initial survey or on user recreation.
   * @param user - user
   */
  public Recommender(User user) {
    this.user = Objects.requireNonNullElseGet(user, User::new);
  }

  /**
   * Overarching make recipe function to be called. Takes query,
   * @param input Search input of user
   * @param paramsMap - parameters for the query the user has entered.
   * @param restrictions - list of restrictions the user has applied to the query.
   * @return List of recommended recipes
   *
   * @throws RecommendationException - algorithm error
   * @throws InterruptedException - query parsing error
   * @throws SQLException - query parsing error
   * @throws APIException - query parsing error
   * @throws IOException - query parsing error
   */
  public List<Recipe> makeRecommendation(String input, Map<String, String[]> paramsMap,
                                         Set<String> restrictions) throws RecommendationException,
          InterruptedException, SQLException, APIException, IOException {
    try {
      this.recipeTree = new KDTree<>(dim);
      // Nutrients: get the weights for each axis based on chosen nutrients and defaults
      List<Double> axisWeights = getNutrientIndices();

      // User History: get, nodify, and normalize previous recipes
      List<RecipeNode> prevRecipeNodes = prepUserHistoryNodes(axisWeights);
      //generate a target node for an ideal recipe using the history, negated
      RecipeNode target = makeTargetNode(prevRecipeNodes);

      Recipe[] recipesArray;
      // Query Recs: get recipes based on the query and put into a queried recipes tree
      recipesArray = FieldParser.getRecipesDBandAPI(input, restrictions, paramsMap);
      List<Recipe> recipesList = Arrays.asList(recipesArray);
      List<RecipeNode> queryNodes = convertRecipesToRecipeNodes(recipesList);


      this.recipeTree.initializeTree(queryNodes);
      // add the target nodes' coordinates to each node in tree to make the origin the target point
      this.recipeTree.translateTree(target.getCoords());
      // normalize the query tree, weighted using the nutrients

      this.recipeTree.normalizeAxes(queryNodes, axisWeights); //weight special axes higher

      // Recommend: return the nearest neighbors to the target (the origin now).
      List<RecipeNode> recNodes = recipeTree.nearestSearch(originNode(), REC_QUANTITY);
      // convert rec nodes to recipes
      List<Recipe> recommendations = new ArrayList<>();
      for (RecipeNode node : recNodes) {
        recommendations.add(node.getRecipe());
      }
      distances = this.recipeTree.getDistances();

      RecipeNode abnormal = getUnnormalizedTargetNode();
      System.out.println("Target unnormal coords");
      for (double c : abnormal.getCoords()) {
        System.out.println(c);
      }
      return recommendations;
    } catch (KDTreeException e) {
      throw new RecommendationException(e.getMessage());
    }
  }

  /**
   * for the gui to print average history values.
   * @return target node with real values
   * @throws RecommendationException on algorithm error
   */
  public RecipeNode getUnnormalizedTargetNode() throws RecommendationException {
    List<Recipe> userHistory = this.user.getPreviousRecipes();
    List<RecipeNode> prevRecipeNodes = this.convertRecipesToRecipeNodes(userHistory);
    // prepare target
    List<Double> coords = new ArrayList<>();
    // default values
    for (int i = 0; i < NutrientInfo.getMainNutrients().size(); i++) {
      coords.add(MAIN_NUT_WEIGHT / 2);
    }
    for (int i = 0; i < NutrientInfo.getSecondaryNutrients().size(); i++) {
      coords.add(SEC_NUT_WEIGHT / 2);
    }
    RecipeNode target = new RecipeNode(coords);
    // set the coords to be the midpoint
    try {
      recipeTree.makeAverageNode(target, prevRecipeNodes);
    } catch (KDTreeException e) {
      throw new RecommendationException(e.getMessage());
    }
    return target;
  }

  /*
   * returns the users history in a form usable for getTarget:
   * gets its recipe history, converts it to nodes, normalizes.
   */
  private List<RecipeNode> prepUserHistoryNodes(List<Double> axisWeights) throws KDTreeException {
    //get, turn into nodes, and normalize user history
    List<Recipe> userHistory = this.user.getPreviousRecipes();
    List<RecipeNode> prevRecipeNodes = this.convertRecipesToRecipeNodes(userHistory);
    this.recipeTree.normalizeAxes(prevRecipeNodes, axisWeights);
    return prevRecipeNodes;
  }

  /*
   * uses the users important nutrients to return indices to be weighted higher in normalize
   */
  private List<Double> getNutrientIndices() {
    List<Double> axisWeights = new ArrayList<>();
    for (int i = 0; i < NutrientInfo.getMainNutrients().size(); i++) {
      axisWeights.add(MAIN_NUT_WEIGHT);
    }
    for (int i = 0; i < NutrientInfo.getSecondaryNutrients().size(); i++) {
      axisWeights.add(SEC_NUT_WEIGHT);
    }
    for (String code : this.user.getNutrients()) {
      int i = NutrientInfo.getNutrientCodes().indexOf(code);
      if (i >= 0 && i < axisWeights.size()) {
        axisWeights.set(i, USER_PREF_WEIGHT);
      }
    }

    return axisWeights;
  }

  /*
   * returns node with dim that is at the origin.
   */
  private RecipeNode originNode() {
    List<Double> origin = new ArrayList<>();
    for (int i = 0; i < this.dim; i++) {
      origin.add(0.);
    }
    return new RecipeNode(origin);
  }

  /*
   * Function to get the coordinates of the starting RecipeNode for nearest neighbor search.
   * @param prevRecipeNodes - the previous recipes that the user has accessed as nodes.
   * @return - a RecipeNode at the coordinates determined by the query and the user's history.
   */
  private RecipeNode makeTargetNode(List<RecipeNode> prevRecipeNodes) throws
          RecommendationException {
    // prepare target
    List<Double> coords = new ArrayList<>();
    // default values
    for (int i = 0; i < NutrientInfo.getMainNutrients().size(); i++) {
      coords.add(MAIN_NUT_WEIGHT / 2);
    }
    for (int i = 0; i < NutrientInfo.getSecondaryNutrients().size(); i++) {
      coords.add(SEC_NUT_WEIGHT / 2);
    }
    RecipeNode target = new RecipeNode(coords);
    // set the coords to be the midpoint
    try {
      recipeTree.makeAverageNode(target, prevRecipeNodes);
    } catch (KDTreeException e) {
      throw new RecommendationException(e.getMessage());
    }

    // negate the values
    List<Double> newCoords = target.getCoords();
    int sz = newCoords.size();
    for (int i = 0; i < sz; i++) {
      newCoords.set(i, -1 * newCoords.get(i));
    }

    return target;
  }

  /*
   * Function to find convert Recipes into RecipeNodes.
   * @param recipes - a list of Recipes to convert to nodes.
   * @return - a list of the converted RecipeNodes.
   */
  private List<RecipeNode> convertRecipesToRecipeNodes(List<Recipe> recipes) {
    List<RecipeNode> nodes = new ArrayList<>();
    for (Recipe recipe : recipes) {
      RecipeNode r = new RecipeNode(recipe);
      this.addRecipeNodeCoords(r);
      nodes.add(r);
    }
    return nodes;
  }

  /*
   * Function to find the coordinates of a RecipeNode based on the user's nutritional preferences.
   * @param r - the RecipeNode to find the coordinates of.
   */
  private void addRecipeNodeCoords(RecipeNode r) {
    List<Double> coords = new ArrayList<>();
    for (String code : NutrientInfo.getNutrientCodes()) {
      coords.add(r.getRecipe().getNutrientVals(code)[0]);
    }
    r.setCoords(coords);
  }

  /**
   * getter.
   * @return dists
   */
  public List<Double> getFoodComaScores() {
    return distances;
  }
}
