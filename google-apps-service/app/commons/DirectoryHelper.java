package commons;
import java.util.List;
import java.util.stream.Collectors;

import akka.actor.UntypedActor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.Directory;

import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.model.*;

import akka.actor.ActorRef;

import commons.GoogleServiceFactory;


public interface DirectoryHelper
{

	List<String> excetuteGetUserGroups(Directory directory, String domain, String user);

}