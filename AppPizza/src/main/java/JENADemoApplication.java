import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;

public class JENADemoApplication {
    public static void main(String[] args) {

        Model model = RDFDataMgr.loadModel("AppPizza.owl");

        String ingredient = "Mozzarella";
        String person = "Gaby_Vargas";
        String group = "Group_Pepa";

        String typeIndividual = "Mozzarella";
        String typeClassReasoner = "PizzaTopping";
        reasonerSubClass(typeIndividual, typeClassReasoner, model);
        findEquivalentTo(ingredient,model);
        findFavorite(group,model);
        findPizzaBan(group,model);
        findRecomender(group,model);
    }

    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s, p, o); i.hasNext(); ) {
            Statement stmt = i.nextStatement();
            System.out.println(" - " + PrintUtil.print(stmt));
        }
    }

    public static void printQuery(String queryString, Model model) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet results = queryExecution.execSelect();

            for (; results.hasNext(); ) {
                QuerySolution querySolution = results.nextSolution();
                System.out.print("\n" + querySolution);

            }

        }
    }

    public static void findEquivalentTo(String ingredient, Model model) {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX pzf: <http://www.pizzafriends.com/ontologies/pizza.owl#>"
                + "SELECT DISTINCT * WHERE { pzf:" + ingredient + " pzf:isEquivalentTo ?equivalentTo"+
                "}";
        printQuery(queryString, model);

    }
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

    public static void findPizzaBan(String group, Model model) {
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX pzf: <http://www.pizzafriends.com/ontologies/pizza.owl#>"
                + "SELECT DISTINCT ?Pizza ?person ?ingredientBan WHERE { pzf:" + group + " pzf:hasMember ?person"
                + "OPTIONAL {?ingredientBan pzf:isBannedToppingOf ?person}"
                + "OPTIONAL {?Pizza rdfs:subClassOf pzf:NamedPizza}"
                + "OPTIONAL {?Pizza pzf:hasTopping ?Ingredients}"
                + "FILTER (?Ingredients = ?ingredientBan)"+
                "}";
        printQuery(queryString, model);

    }
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


}