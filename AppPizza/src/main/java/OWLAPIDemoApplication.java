import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OWLAPIDemoApplication {

    public static void main(String[] args) throws OWLOntologyCreationException {
        // Model model = RDFDataMgr.loadModel("PizzaTutorial.owl");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o;
        OWLDataFactory df = man.getOWLDataFactory();
        IRI pizzaontology = IRI.create("http://www.pizzafriends.com/ontologies/pizza.owl");
        try {
            o = man.loadOntology(pizzaontology);
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner r = rf.createReasoner(o);
            r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            r.getSubClasses(df.getOWLClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseyPizza"), true).forEach(System.out::println);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }
}

