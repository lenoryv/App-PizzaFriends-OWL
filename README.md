# PizzaFriends-OWL
Aplicación Pizza Friends

Dentro del código de la aplicación se crea el modelo de la ontología y se instala el razonador que trabajará con dicha ontología.
```java
Model model = RDFDataMgr.loadModel("AppPizza.owl");
```
Luego se procede a construir las consultas para obtener las pizzas recomendadas según los ingredientes preferidos y baneados de los clientes que pertenecen a un determinado grupo de amistad
```java
String group = "Group_Pepa";
```
Buscar Ingredientes Favoritos - Grupo de Amistad llamado "Pepa"
```java
public static void findFavorite(String group, Model model) {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX pzf: <http://www.pizzafriends.com/ontologies/pizza.owl#>"
                + "SELECT DISTINCT ?person ?favorite WHERE { pzf:" + group + " pzf:hasMember ?person"
                + "OPTIONAL {?person pzf:hasFavoriteTopping ?favorite}" +
                "}";
        printQuery(queryString, model);

    }
```
Buscar Recomendación para el Grupo
```java
    public static void findRecomender(String group, Model model) {
        String queryString = "prefix pzf: <http://www.pizzafriends.com/ontologies/pizza.owl#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX schema: <http://schema.org/>\n" +
                "\n" +
                "SELECT ?PizzasRecomender\n" +
                "WHERE {\n" +
                "  ?PizzaType rdfs:subClassOf pzf:NamedPizza\n" +
                "  OPTIONAL {?PizzasList rdf:type ?PizzaType}\n" +
                "  OPTIONAL {pzf:"+group+" pzf:hasMember ?person}\n" +
                "  OPTIONAL {?Favorite pzf:isFavoriteOf ?person}\n" +
                "  OPTIONAL {?PizzasRecomender pzf:hasTopping ?Favorite}\n" +
                "  FILTER (?PizzasRecomender != ?PizzaBan)\n" +
                "  {\n" +
                "    SELECT *\n" +
                "    WHERE {\n" +
                "\t\t?Ban pzf:isToppingOf ?PizzasList\n" +
                "      \tBIND (?PizzasList As ?PizzaBan)\n" +
                "      {\n" +
                "        SELECT *\n" +
                "    \tWHERE {\n" +
                "         \tpzf:"+group+" pzf:hasMember ?person\n" +
                "      \t\tOPTIONAL {?Banned pzf:isBannedToppingOf ?person}\n" +
                "          BIND (?Banned As ?Ban)\n" +
                "          FILTER (?Banned = ?Ban)\n" +
                "    \t} \n" +
                "    \t}\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        printQuery(queryString, model);

    }
```
Razonador 
```java
    public static void reasonerSubClass(String subClass, String classModel, Model model) {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(model);
        InfModel infmodel = ModelFactory.createInfModel(reasoner, model);

        Resource pizza = infmodel.getResource("http://www.pizzafriends.com/ontologies/pizza.owl#" + classModel);
        Resource typePizza = infmodel.getResource("http://www.pizzafriends.com/ontologies/pizza.owl#" + subClass);
        if (infmodel.contains(typePizza, RDF.type, pizza)) {
            System.out.println(subClass + " es " + classModel);
        } else {
            System.out.println(subClass + " no es " + classModel);
        }
    }
    
    ```
