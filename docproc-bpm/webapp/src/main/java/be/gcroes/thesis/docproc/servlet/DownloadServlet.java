package be.gcroes.thesis.docproc.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.gcroes.thesis.docproc.entity.EntityManagerUtil;
import be.gcroes.thesis.docproc.entity.Job;
import be.gcroes.thesis.docproc.entity.Task;

public class DownloadServlet extends HttpServlet {

    private static final int BUFSIZE = 4096;

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
                        "SELECT j FROM Job j WHERE j.activitiJobId = " + jobId
                                + " AND j.user = \'" + user + "\'", Job.class)
                        .getSingleResult();
                String filepath = job.getResult();
                sendFile(request, response, filepath);
            }
            if (jobId != null && taskId != null) {
                Task task = em
                        .createQuery("SELECT t FROM Task t, Job j WHERE" +
                                  " t.id = " + taskId
                                + " AND j.activitiJobId = " + jobId
                                + " AND j.user = \'" + user + "\'"
                                + " AND t.job = j", Task.class).getSingleResult();
                String filepath = task.getResult();
                sendFile(request, response, filepath);
            }
        } else {
            response.getWriter().write("Access denied. Please login before downloading a file.");
        }

    }

    private void sendFile(HttpServletRequest request,
            HttpServletResponse response, String filepath) throws IOException {
       File file = new File(filepath);
       int length = 0;
       ServletOutputStream outStream = response.getOutputStream();
       ServletContext context = getServletConfig().getServletContext();
       String mimetype = context.getMimeType(filepath);
       if(mimetype == null){
           mimetype = "application/octet-stream";
       }
       response.setContentType(mimetype);
       response.setContentLength((int)file.length());
       String fileName = (new File(filepath).getName());
       response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
       
       byte[] bytebuffer = new byte[BUFSIZE];
       DataInputStream in = new DataInputStream(new FileInputStream(filepath));
       while ( (in != null) && ((length = in.read(bytebuffer)) != -1)){
           outStream.write(bytebuffer, 0, length);
       }
       in.close();
       outStream.close();
    }

}
