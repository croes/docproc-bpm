package be.gcroes.thesis.docproc.servlet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;
import be.gcroes.thesis.docproc.messaging.EndPoint;

public class DownloadServlet extends HttpServlet {

    private static final int BUFSIZE = 4096;
    
    private static Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

    /**
     * 
     */
    private static final long serialVersionUID = -5564482169209224817L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String jobId = request.getParameter("jobId");
        String taskId = request.getParameter("taskId");
        if (session != null) {
            String user = (String) session.getAttribute("user");
            EntityManagerFactory emf = EntityManagerUtil
                    .getEntityManagerFactory();
            EntityManager em = emf.createEntityManager();
            if (jobId != null && taskId == null) {
                Job job = em.createQuery(
                        "SELECT j FROM Job j WHERE j.id = " + jobId
                                + " AND j.user = \'" + user + "\'", Job.class)
                        .getSingleResult();
                byte[] result = job.getResult();
                sendFile(request, response, result);
            }
            if (jobId != null && taskId != null) {
                Task task = em
                        .createQuery("SELECT t FROM Task t, Job j WHERE" +
                                  " t.id = " + taskId
                                + " AND j.id = " + jobId
                                + " AND j.user = \'" + user + "\'"
                                + " AND t.job = j", Task.class).getSingleResult();
                byte[] result = task.getResult();
                sendFile(request, response, result);
            }
        } else {
            response.getWriter().write("Access denied. Please login before downloading a file.");
        }

    }

    private void sendFile(HttpServletRequest request,
            HttpServletResponse response, byte[] data) throws IOException {
       ServletOutputStream outStream = response.getOutputStream();
       //ServletContext context = getServletConfig().getServletContext();
       //String mimetype = context.getMimeType(filepath);
       String mimetype = "application/octet-stream";
       response.setContentType(mimetype);
       response.setContentLength(data.length);
       //String fileName = (new File(filepath).getName());
       response.setHeader("Content-Disposition", "attachment; filename=\"result.pdf\"");
       
       byte[] bytebuffer = new byte[BUFSIZE];
       DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
       int length = 0;
       while ( (in != null) && ((length = in.read(bytebuffer)) != -1)){
           outStream.write(bytebuffer, 0, length);
       }
       in.close();
       outStream.close();
    }

}
