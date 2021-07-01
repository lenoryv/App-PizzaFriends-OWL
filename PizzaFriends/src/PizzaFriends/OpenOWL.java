
package PizzaFriends;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

//Class OpenOWL

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

class OpenOWL {
   static  String  s;
   
   //Conexion
    static  OntModel OpenConnectOWL(){
        
    OntModel mode = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_RULE_INF);
    java.io.InputStream in = FileManager.get().open( "C:/PizzaFriends" ); 
    if (in == null) {
        throw new IllegalArgumentException("Archivo de ontología no encontrado");
    }
        return  (OntModel) mode.read(in, "");
    }

     static  com.hp.hpl.jena.query.ResultSet ExecSparQl(String Query){
         
          com.hp.hpl.jena.query.Query query = QueryFactory.create(Query);
                QueryExecution qe = QueryExecutionFactory.create(query, OpenConnectOWL());
                com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
                
                return results;
         
     }
     
      static  String GetResultAsString(String Query){
        try {
            com.hp.hpl.jena.query.Query query = QueryFactory.create(Query);
                  QueryExecution qe = QueryExecutionFactory.create(query, OpenConnectOWL());
                  com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
                  if(results.hasNext()){
                     ByteArrayOutputStream go = new ByteArrayOutputStream ();
                   ResultSetFormatter.out((OutputStream)go ,results, query);
                       s = new String(go.toByteArray(), "UTF-8");
                   }
                  else{
                      s = "Sin resultado";
                  }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OpenOWL.class.getName()).log(Level.SEVERE, null, ex);
        }
         return s;
      }
    
}