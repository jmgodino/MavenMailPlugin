package com.jccm.edu.maven;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jccm.edu.maven.mail.MailHelper;
import com.jccm.edu.maven.velocity.VelocityHelper;

/**
 * @goal mail
 */
public class MavenMailPlugin extends AbstractMojo {

    public MavenMailPlugin() {
    }

    public void execute() throws MojoExecutionException {
        try {
        	
        	// NO se ejecuta la condicion para desplegar el proyecto POM Padre
        	if (project.getName().equalsIgnoreCase("DeployableProject")) {
        		return;
        	}
        	if (System.getProperty("skipMail") != null) {
       		    getLog().info("skipMail. No se enviara el correo");
        		return;
        	}
        	if ("local".equalsIgnoreCase(project.getProperties().getProperty("entorno"))) {
        		getLog().info("entorno LOCAL. No se enviara el correo");
        		return;
        	}
        	
        	
	        String msg = new VelocityHelper().getTemplateMessage(project, encoding, template, deploymentRepository.getUrl(), servidorApp, entorno, fechaHora);
	        MailHelper mailer = createMailer();

	        getLog().info((new StringBuilder()).append("Conectando al servidor de correo: ").append(smtpHost).append(":").append(smtpPort).toString());
            mailer.send(title, msg, toAddresses, from, encoding);
            getLog().info("Correo enviado");
        } catch(Exception e) {
            throw new MojoExecutionException((new StringBuilder()).append("Failed to send email for reason ").append(e.getMessage()).toString(), e);
        }
    }



    protected MailHelper createMailer() throws NoSuchAlgorithmException {
        MailHelper mailer = new MailHelper();

        mailer.setSmtpHost(smtpHost);
        mailer.setSmtpPort(smtpPort);
        mailer.setSslMode(sslMode);
        if(username != null)
            mailer.setUsername(username);
        if(password != null)
            mailer.setPassword(password);
        mailer.initialize();
        return mailer;
    }



    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }



    /**
     * @parameter expression="${procesarMailTemplate.title}"  default-value=""
     */
    protected String title;
    
    /**
     * @parameter expression="${procesarMailTemplate.smtpHost}" default-value="smtp.jccm.es"
     */    
    protected String smtpHost;
    
    /**
     * @parameter expression="${procesarMailTemplate.smtpPort}" default-value="25"
     */    
    protected int smtpPort;
    
    /**
     * @parameter expression="${procesarMailTemplate.username}" default-value=""
     */    
    protected String username;
    
    /**
     * @parameter expression="${procesarMailTemplate.password}" default-value=""
     */    
    protected String password;
    
    /**
     * @parameter expression="${procesarMailTemplate.sslMode}" default-value=false
     */    
    protected boolean sslMode;
    
    /**
     * @parameter expression="${procesarMailTemplate.from}" default-value="desarrollo.edu@jccm.es"
     */
    protected String from;
    
    /**
     * @parameter expression="${procesarMailTemplate.toAddresses}"  default-value=""
     */    
    protected List<String> toAddresses;
    
    
    
	/** @parameter default-value="${project}" */
	private MavenProject project;	
	
	/** @parameter default-value="${project.distributionManagementArtifactRepository}" */
	private ArtifactRepository deploymentRepository;	
	
	/**
     * Template a procesar
     * @parameter expression="${procesarMailTemplate.template}"  default-value=""
     */
    private String template;
    
    /**
     * Entorno de despliegue
     * @parameter expression="${procesarMailTemplate.entorno}" default-value="" 
     */
    private String entorno;    
    
    /**
     * Servidor de despliegue
     * @parameter expression="${procesarMailTemplate.servidorApp}"
     */
    private String servidorApp;    
    
    /**
     * Fecha y hora de despliegue
     * @parameter expression="${procesarMailTemplate.fechaHora}"
     */
    private String fechaHora;   
    
    /**
     * @parameter expression="${procesarMailTemplate.encoding}" default-value="ISO-8859-1"
     */
    private String encoding;    
    

}
