let d = new Date();
console.log("hello??");


$(document).ready(() => {
    console.log("second print line!");
    let postParams = {
        url: window.location.href,
        username: getCookie("username")
    };
    console.log(postParams.url);
    getRecipes(postParams);

    // $.post("/recipe/recipeuri", postParams, response => {
    //     console.log("We made a get request!");
    //     console.log(postParams.url);
    //     // console.log(response);
    //     // const output = JSON.parse(response);
    //     // Do something with the response here
    // });
    // document.getElementById("validity").innerHTML = "START";
});


function getRecipes(params){
    return $.post("/recipe/recipeuri", params, response =>{
        let obj = JSON.parse(response);
        document.getElementById("title").innerHTML = " <h1><a target=_blank href=\"" + obj.URL + "\"> " + obj.title + " </a></h1>";
        console.log(obj.URL);
        document.getElementById("title").href = obj.URL;
        $.each(obj.recipeList, function printRecipe(key, value){
            console.log(key + ": " + value[0]);
            document.getElementById("recipes").innerHTML += " <h6 id=\"recipes\"><a href=\"/recipe/" + key + "\"> " + value[0] + " </a></h6>";
            document.getElementById("foodImage").src = obj.image;
        })
        for(let i = 0; i < obj.ingredients.length; i ++){
            document.getElementById("ingredients").innerHTML += obj.ingredients[i] + "</br>";
            console.log(obj.ingredients[i]);
        }
        document.getElementById("nutrients").innerHTML += obj.Nutrients[0] + ": " + obj.Nutrients[1] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[2] + ": " + obj.Nutrients[3] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[4] + ": " + obj.Nutrients[5] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[6] + ": " + obj.Nutrients[7] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[8] + ": " + obj.Nutrients[9] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[10] + ": " + obj.Nutrients[11] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[12] + ": " + obj.Nutrients[13] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[14] + ": " + obj.Nutrients[15] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[16] + ": " + obj.Nutrients[17] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[18] + ": " + obj.Nutrients[19] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[20] + ": " + obj.Nutrients[21] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[22] + ": " + obj.Nutrients[23] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[24] + ": " + obj.Nutrients[25] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[26] + ": " + obj.Nutrients[27] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[28] + ": " + obj.Nutrients[29] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[30] + ": " + obj.Nutrients[31] + " kcal <br>";
        document.getElementById("nutrients").innerHTML += obj.Nutrients[32] + ": " + obj.Nutrients[33] + " kcal <br>";

    });
};

