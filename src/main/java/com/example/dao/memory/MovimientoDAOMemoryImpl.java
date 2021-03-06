package com.example.dao.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.example.dao.CuentaDAO;
import com.example.dao.MovimientoDAO;
import com.example.factory.DAOFactory;
import com.example.factory.DAOFactory.DAOTYPE;
import com.example.model.Cuenta;
import com.example.model.Movimiento;

/**
 * Implementacion en memoria de Movimientos
 * 
 * @author Gustavo Bazan
 *
 */
public class MovimientoDAOMemoryImpl implements MovimientoDAO {
    /** Singleton instance */
	private static final MovimientoDAOMemoryImpl INSTANCE = new MovimientoDAOMemoryImpl();
    /** Logger */
    private static final Logger logger = Logger.getLogger(MovimientoDAOMemoryImpl.class.getCanonicalName());
    /** Data */
    private static Vector<Movimiento> movimientos;
    /** Threadsafe ID controller */
    private static AtomicInteger LAST_ID;

    private Integer incrementCount() {
        return LAST_ID.incrementAndGet();
    }
	
    private MovimientoDAOMemoryImpl(){
        movimientos = new Vector<>();
        LAST_ID = new AtomicInteger(0);
    }
	
    public static MovimientoDAOMemoryImpl getInstance() {
        return INSTANCE;
    }
	
    @Override
    public Movimiento create(Movimiento movimiento) {
        movimiento.setId(incrementCount());
        movimientos.add(movimiento);
        logger.info(movimiento.toString());
        return movimiento;
    }

    @Override
    public boolean update(Movimiento movimiento) {
        int index = movimientos.indexOf(movimiento);
        if(index < 0)
            return false;
        movimientos.remove(index);
        if(index < movimientos.size())
            movimientos.add(index, movimiento);
        else
            movimientos.add(movimiento);
        return true;
    }

    @Override
    public boolean remove(Movimiento movimiento) {		
        return movimientos.remove(movimiento);
    }

    @Override
    public Movimiento find(Integer id) {
        Movimiento movimiento = null;
        for(Movimiento o : movimientos){
            if(o.getId().equals(id)){
                movimiento = o;
                break;
            }
        }
        return movimiento;
    }

    @Override
    public Movimiento first() {		 
        try {
            return movimientos.firstElement();
        } catch (NoSuchElementException ex){
            logger.warning(ex.getMessage());
            return null;
        }
    }

    @Override
    public Movimiento last() {
        try {
            return movimientos.lastElement();
        } catch (NoSuchElementException ex){
            logger.warning(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Movimiento> findAll() {		
        return movimientos;
    }

    @Override
    public Integer count(){
        return movimientos.size();
    }

    public void empty() {
        movimientos.clear();
        LAST_ID = new AtomicInteger(0);
    }

	@Override
	public List<Movimiento> findMovimientosCuenta(Integer cuenta_id) {
		DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOTYPE.MEMORYFACTORY);
		CuentaDAO cuentaDao = daoFactory.getCuentaDAO();		
        Cuenta cuenta = cuentaDao.find(cuenta_id);
        List<Movimiento> movimientosTemp = new  ArrayList<Movimiento>();

        for(Movimiento movimiento : movimientos){
            if(movimiento.getCuenta().equals(cuenta))
            	movimientosTemp.add(movimiento);
        }
        return movimientosTemp;
	}

}
