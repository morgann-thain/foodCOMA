
$(document).ready(() => {
    console.log("search console log -- hello??");

});

const preferences = $("#preferences");
const button = $("#submit");
console.log("HERE " + preferences.val());

$(document).ready(function(){
    $('#preferences').keypress(function(e){
        if(e.keyCode==13)
            $('#submit').click();
    });
});

button.click(event => {
    const balanced = $("#balanced").is(":checked");
    const lowfat = $("#low-fat").is(":checked");
    const lowcarb = $("#low-carb").is(":checked");
    const highprotein = $("#high-protein").is(":checked");
    const vegan = $("#vegan").is(":checked");
    const vegetarian = $("#vegetarian").is(":checked");
    const sugarconscious = $("#sugar-conscious").is(":checked");
    const peanutfree = $("#peanut-free").is(":checked");
    const treenutfree = $("#tree-nut-free").is(":checked");
    const alcoholfree = $("#alcohol-free").is(":checked");


    var donut = document.getElementById("rollingDonut");
    var newDonut = donut.cloneNode(true);
    donut.parentNode.replaceChild(newDonut, donut);
    document.getElementById("rollingDonut").src = "https://i.postimg.cc/c4XzqfF1/image.png";

    const postParameters = {
        //TODO: get the text inside the input box
        prefs: preferences.val(),
        username: getCookie("username"),
        vg: vegan,
        veg: vegetarian,
        sug: sugarconscious,
        pf: peanutfree,
        tf: treenutfree,
        af: alcoholfree
    };
    //TODO: make a post request to the url to handle this request you set in your Main.java

    $.post("/search", postParameters, response => {
        console.log("We made a post request!");
        const output = JSON.parse(response);
        // document.getElementById("#preferences").innerHTML = "";
        let i = 0;
        $.each(output.simpleRecipeList, function printRecipe(index, key){
            // document.getElementById("container").innerHTML +=
            console.log("Inside each statement simpleRecipeList");
            console.log("Index: " + index);
            console.log("Key: " + key);
            console.log("Key 0: " + key[0]);
            console.log("Key 1: " + key[1]);

            // document.getElementById("container").innerHTML += "Name: " + index + " URL: " + key[0];
            // document.getElementById("container").innerHTML += "HELLO?";

            document.getElementById("container").innerHTML += "<h6><a href = \"recipe/" + key[1] + "\">" + index + " </a></h6>";
            if(i < 10){
                console.log("shoppingBag" + i);
                var fullShopBagHtml = "<img src=\"https://i.postimg.cc/FK87G91b/bag-161440-1280.png\" class=\"shoppingBag\"><a class=\"recipeText\" href = \"recipe/" + key[1] + "\">" + index + " </a>"

                // document.getElementById("shoppingBag" + i).innerHTML += shoppingBagHtml;

                document.getElementById("shoppingBag" + i).innerHTML = fullShopBagHtml.toString();
                // document.getElementById("shoppingBag" + i).innerHTML += "<a class=\"recipeText\" href = \"recipe/" + key[1] + "\">" + "hello??????????????????" + " </a>";

            }

            i++;
            // document.getElementById("container").innerHTML += " <h6><a href=\"/recipe/" + key[1] + "\"> " + index + " </a></h6>";

        });
        // for(i = 0; i < output.recipes.length; i++){
        //     console.log(output.recipes[i].getLabel());
        // }
        console.log("RECIPE SIZE:");
        console.log(output.recipes.length);
        if(output.recipes.length == 0){
            for(let i = 0; i < 10; i++){
                var fullShopBagHtml = "<img class=\"shoppingBag\"><a class=\"recipeText\" </a>"
                document.getElementById("shoppingBag" + i).innerHTML = fullShopBagHtml.toString();
            }
            document.getElementById("isEmpty").innerHTML = "No search results :(";
        } else if (output.recipes.length != 0){
            document.getElementById("isEmpty").innerHTML = "";
        }
    });


    var options = [];

    $( '.dropdown-menu a' ).on( 'click', function( event ) {

        var $target = $( event.currentTarget ),
            val = $target.attr( 'data-value' ),
            $inp = $target.find( 'input' ),
            idx;

        if ( ( idx = options.indexOf( val ) ) > -1 ) {
            options.splice( idx, 1 );
            setTimeout( function() { $inp.prop( 'checked', false ) }, 0);
        } else {
            options.push( val );
            setTimeout( function() { $inp.prop( 'checked', true ) }, 0);
        }

        $( event.target ).blur();

        console.log( options );
        return false;
    });

});

function toggleNutrition(nutrient){
    console.log("NUTRIENT CALLED: " + nutrient);
    // const postParameters = {
    //     nut: nutrient
    // };
    // $.post("/toggleNutrient", postParameters);
}

