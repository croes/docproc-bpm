package be.gcroes.thesis.docproc.servlet;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.messaging.QueueMessage;

/**
 * Servlet implementation class AddJob
 */
public class AddJob extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private EntityManager em;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddJob() {
        super();
        
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		em = EntityManagerUtil.getEntityManagerFactory().createEntityManager();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Job job = new Job();
		job.setInputdata(request.getParameter("data"));
		job.setTemplate(request.getParameter("template"));
		job.setUser(request.getUserPrincipal().getName());
		String validationResult = validateJob(job);
		if(validationResult == null){
			EntityTransaction tx = em.getTransaction();
			try{
				em.persist(job);
				QueueMessage message = new QueueMessage("csv-to-data", job);
				message.send();
				tx.commit();
			}catch(IOException ioe){
				ioe.printStackTrace();
				response.sendError(500, "Could not place job in queue");
				tx.rollback();
			}
		}else{
			response.sendError(400, validationResult);
		}
	}
	
	private String validateJob(Job job){
		String reason = null;
		if(job.getInputdata() == null){
			reason += "invalid csv input data.\n";
		}
		if(job.getTemplate() == null){
			reason += "invalid template \n";
		}
		if(job.getUser() == null){
			reason += "invalid user \n";
		}
		return reason;
	}

}
