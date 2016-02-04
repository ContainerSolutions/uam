package com.diosoft.uam.db;

import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.entry.AccountEntry;
import com.diosoft.uam.db.entry.UserEntry;
import com.google.inject.Inject;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import play.Logger;

public class AccountStorage {
    private static final Logger.ALogger LOG = Logger.of(AccountStorage.class);

    private final OrientGraphFactory graphFactory;

    @Inject
    public AccountStorage(OrientGraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }

    public void saveAccount(AccountEntry entry) throws AccessManagerException {
        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex user = getUserVertex(entry.getId(), graph);

            OrientVertex adAccountVertex = graph.addVertex(AccountEntry.VERTEX_AD_ACCOUNT, "adaccount");
            adAccountVertex.setProperty(AccountEntry.PROPERTY_USER_ID, entry.getId());
            adAccountVertex.setProperty(AccountEntry.PROPERTY_FIRST_NAME, entry.getFirstName());
            adAccountVertex.setProperty(AccountEntry.PROPERTY_LAST_NAME, entry.getLastName());
            adAccountVertex.setProperty(AccountEntry.PROPERTY_EMAIL, entry.getEmail());

            user.addEdge(UserEntry.PROPERTY_HAS_ACCOUNT, adAccountVertex);

            graph.commit();
            LOG.info("Successfully saved account entry for " + entry.getId());
        } catch (Exception e) {
            graph.rollback();
            String errorMessage = "Can't save account entry for " + entry.getId();
            LOG.error(errorMessage, e);
            throw new AccessManagerException(errorMessage, e);
        } finally {
            graph.shutdown();
        }
    }

    public void deleteAccount(String id) throws AccessManagerException {
        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex delete = null;
            for (Vertex account : graph.getVerticesOfClass(AccountEntry.VERTEX_AD_ACCOUNT)) {
                if (id.equals(account.getProperty(AccountEntry.PROPERTY_USER_ID))) {
                    delete = account;
                    break;
                }
            }
            if(delete == null) {
                throw new IllegalStateException("Can't find account entry for " + id);
            } else {
                graph.removeVertex(delete);
                //TODO should account edge be deleted in user?
                graph.commit();
                LOG.info("Successfully deleted account entry for " + id);
            }
        } catch (Exception e) {
            graph.rollback();
            String errorMessage = "Can't delete account entry for " + id;
            LOG.error(errorMessage, e);
            throw new AccessManagerException(errorMessage, e);
        } finally {
            graph.shutdown();
        }

    }

    private Vertex getUserVertex(String userUniqueId, OrientBaseGraph graph) throws Exception {
        for (Vertex user : graph.getVerticesOfClass(UserEntry.VERTEX_USER)) {
            if (userUniqueId.equals(user.getProperty(UserEntry.PROPERTY_UNIQUE_ID))) {
                return user;
            }
        }
        throw new IllegalStateException("Can't find user vertex for " + userUniqueId);
    }

}
