/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package economistworkstation.Util;

import economistworkstation.ContractDataParameters;
import economistworkstation.Entity.AccumulationDocument;
import economistworkstation.Entity.Document;

/**
 *
 * @author fnajer
 */
public class AccumulationStatement extends Statement {
    public AccumulationStatement() {
        super();
        
        path = getPatternsPath("accumulationStatement.xls");
    }
    
    @Override
    public void print(ContractDataParameters data) {
        Document doc = new AccumulationDocument(
                data,
                path,
                data.getSavePath());
        doc.print();
    }
    
    @Override
    public String toString() {
        return String.format("Накопительная ведомость");
    }
}
