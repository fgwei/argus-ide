package argus.tools.eclipse.contribution.weaving.jdt.indexerprovider;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.internal.core.search.indexing.AbstractIndexer;
import org.eclipse.jdt.internal.core.search.JavaSearchParticipant;

import argus.tools.eclipse.contribution.weaving.jdt.ArgusJDTWeavingPlugin;

@SuppressWarnings("restriction")
public aspect IndexerProviderAspect {

  pointcut indexDocument(SearchDocument document, IPath indexPath) : 
    execution(public void JavaSearchParticipant.indexDocument(SearchDocument, IPath)) &&
    args(document, indexPath);

  void around(SearchDocument document, IPath indexPath) : 
    indexDocument(document, indexPath) {

    String path = document.getPath();
    if (path.endsWith(".pilar") || path.endsWith(".plr")) {
      for (IIndexerFactory provider : IndexerProviderRegistry.getInstance().getProviders()) {
        try {
          AbstractIndexer indexer = provider.createIndexer(document);
          indexer.indexDocument();
        } catch (Throwable t) {
          ArgusJDTWeavingPlugin.logException(t);
        }
      }
    } else {
      proceed(document, indexPath);
    }
  }

}
