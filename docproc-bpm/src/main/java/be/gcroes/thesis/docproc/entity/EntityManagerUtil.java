package be.gcroes.thesis.docproc.entity;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerUtil
{
   public static EntityManagerFactory getTestEntityManagerFactory()
   {
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
      return emf;
   }
}
