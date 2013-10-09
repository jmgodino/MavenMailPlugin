package com.jccm.edu.maven.velocity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class VelocityHelper {



	public String getTemplateMessage(MavenProject project, String encoding, String template, String url, String servidorApp, String entorno, String fechaHora) throws Exception {
			VelocityEngine ve = new VelocityEngine();
			ve.init();
			Template t = ve.getTemplate(template);
			VelocityContext context = new VelocityContext();
			t.setEncoding(encoding);
			
			String groupDir = project.getGroupId().replace('.', '/');
			
			context.put("artifactId",project.getArtifactId());
			context.put("groupId",project.getGroupId());
			context.put("version",project.getVersion());
			context.put("servidorApp",servidorApp);
			context.put("entorno",entorno);
			context.put("fechaHora",fechaHora);
			
			context.put("downloadURL",url+"/"+groupDir+"/"+project.getArtifactId()+"/"+project.getVersion()+"/"+project.getArtifactId()+"-"+project.getVersion()+"-"+entorno+"."+project.getPackaging());
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(bos,encoding);
			t.merge(context, writer);
			writer.flush();
			writer.close();			
			String resultado = bos.toString(encoding);
			
			return resultado;
     
    }
}