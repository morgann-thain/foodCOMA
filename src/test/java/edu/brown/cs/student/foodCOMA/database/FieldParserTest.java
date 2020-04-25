package edu.brown.cs.student.foodCOMA.database;

import edu.brown.cs.student.database.APIException;
import edu.brown.cs.student.database.FieldParser;
import edu.brown.cs.student.food.NutrientInfo;
import edu.brown.cs.student.food.Recipe;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

/**
 * This class tests the methods contained within the FieldParser class.
 */
public class FieldParserTest {

  @Test
  public void testGetRecipesFromQuery() {
    NutrientInfo.createNutrientsList();
    try {
      Recipe[] recipes = FieldParser.getRecipesFromQuery("chicken");
      assertNotNull(recipes);
      for (Recipe r : recipes) {
        assertNotNull(r);
        assertNotNull(r.getUri());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException | APIException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
