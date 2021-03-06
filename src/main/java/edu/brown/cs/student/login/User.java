package edu.brown.cs.student.login;

import edu.brown.cs.student.database.APIException;
import edu.brown.cs.student.database.RecipeDatabase;

import edu.brown.cs.student.food.Recipe;
import edu.brown.cs.student.recommendation.Recommender;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for a User, storing username, hashed password, and salt used for hashing to raw password
 * in csv upon construction and holding in its fields the user's username, saved recipes, and
 * dietary restrictions.
 */
public class User {
  /**
   * username - the unique username of the user.
   * previousRecipes - a list of Recipe objects the user has previously accessed.
   * dietaryRestrictions - a list of health labels.
   * nutrients - a list of nutrient codes that the user has specified.
   */
  private final String username;
  private final List<Recipe> previousRecipes;
  private final List<String> dietaryRestrictions;
  private List<String> nutrients;
  private Recommender recommender;
  private static final int NUM_HISTORY = 50;

  /**
   * Constructors for adding a new User on sign up.
   * @param user name
   * @param password password
   * @throws AccountException on write failure, just propagate message to handle.
   */
  public User(String user, String password) throws AccountException {
    this.username = user;
    this.previousRecipes = new ArrayList<>(); // initialized
    this.dietaryRestrictions = new ArrayList<>(); // initialized w/ survey in GUI
    this.nutrients = new ArrayList<>();
    Accounts.writeLoginInfo(user, password); // write the login info to our csv
    this.recommender = null;
    // add to user map
    Accounts.addUserMap(this);
    // add user to user database
    try {
      UserDatabase.insertUser(this);
    } catch (SQLException e) {
      throw new AccountException(e.getMessage());
    }
  }
  /*
   * testing constructor
   */
  protected User(String username, String password, String path) throws AccountException {
    this.username = username;
    this.previousRecipes = new ArrayList<>();
    this.dietaryRestrictions = new ArrayList<>();
    this.nutrients = new ArrayList<>();
    // write the login info to any csv (for testing)
    Accounts.writeLoginInfo(username, password, path);
    // create a personal recommender
    this.recommender = null;
    // don't add test users to map or user database
  }

  /**
   * empty user for null occasions.
   */
  public User() {
    this.username = "";
    this.previousRecipes = new ArrayList<>();
    this.dietaryRestrictions = new ArrayList<>();
    this.nutrients = new ArrayList<>();
    this.recommender = null;
  }

  /**
   * Constructor for recreating a User from the UserDatabase.
   * @param username - name
   * @param previousRecipes - list
   * @param dietaryRestrictions - list
   * @param nutrients - list
   */
  public User(String username, List<Recipe> previousRecipes, List<String> dietaryRestrictions,
              List<String> nutrients) {
    this.username = username;
    this.previousRecipes = previousRecipes;
    this.dietaryRestrictions = dietaryRestrictions;
    this.nutrients = nutrients;
    this.recommender = null;
  }
  /*
   * testing
   */
  protected User(String username) {
    this.username = username;
    this.previousRecipes = new ArrayList<>();
    this.dietaryRestrictions = new ArrayList<>();
    this.nutrients = new ArrayList<>();
    this.recommender = new Recommender(this);
  }



  /**
   * username getter.
   * @return username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Function to return the previousRecipes field.
   * @return - previousRecipes.
   */
  public List<Recipe> getPreviousRecipes() {
    return previousRecipes;
  }

  /**
   * Function to add to the previousRecipes field given the uri of a Recipe.
   * @param uri - the uri of the Recipe to add to previousRecpes.
   * @throws InterruptedException concurrency
   * @throws SQLException on query
   * @throws APIException on query
   * @throws IOException on query
   */
  public void addToPreviousRecipesByURI(String uri) throws InterruptedException, SQLException,
      APIException, IOException {
    Recipe r = RecipeDatabase.getRecipeFromURI(uri);
    UserDatabase.insertToPrevRecipe(this.username, uri);
    this.previousRecipes.add(r);

    if (this.previousRecipes.size() > NUM_HISTORY) {
      this.previousRecipes.remove(this.previousRecipes.size() - 1);
    }
  }

  /**
   * Function to get the dietary restrictions of a user.
   * @return - the dietaryRestrictions field.
   */
  public List<String> getDietaryRestrictions() {
    //returning defensive copy.
    return new ArrayList<>(this.dietaryRestrictions);
  }

  /**
   * getter.
   * @return nutrients
   */
  public List<String> getNutrients() {
    return this.nutrients;
  }

  /**
   * setter.
   * @param nutrients - list
   * @throws SQLException on database insert
   */
  public void setNutrients(List<String> nutrients) throws SQLException {
    for (String nutrient : nutrients) {
      UserDatabase.insertToNutrients(this.username, nutrient);
    }
    this.nutrients = nutrients;
  }

  /**
   * getter.
   * @return rec
   */
  public Recommender getRecommender() {
    return this.recommender;
  }

  /**
   * setter.
   * @param r rec
   */
  public void setRecommender(Recommender r) {
    this.recommender = r;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return this.username.hashCode();
  }
}


