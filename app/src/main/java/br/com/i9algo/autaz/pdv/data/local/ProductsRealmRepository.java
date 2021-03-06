package br.com.i9algo.autaz.pdv.data.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.i9algo.autaz.pdv.domain.models.Product;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProductsRealmRepository {
	
	private static final String TAG = ProductsRealmRepository.class.getSimpleName();
	
	public static synchronized boolean isExisting(String token) {
		Realm realm = Realm.getDefaultInstance();
		Product result = realm.where(Product.class).equalTo(Product.getRouteKeyName(), token).findFirst();
		return (result != null);
	}

	public static synchronized long count() {
		Realm realm = Realm.getDefaultInstance();
		long result = realm.where(Product.class).count();
		return (result);
	}

	public static List<Product> getAll() {
		Realm realm = Realm.getDefaultInstance();
		RealmResults<Product> result = realm.where(Product.class).findAll();
		result = result.sort("name"); // Sort ascending
		return new ArrayList<Product>(result);
	}

	public static Product getById(String token) {
		Realm realm = Realm.getDefaultInstance();
		Product model = null;
		try {
			model = realm.where(Product.class).equalTo(Product.getRouteKeyName(), token).findFirst();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//realm.close();
		}
		return model;
	}

	public static Product getLastUpdated() {
		Realm realm = Realm.getDefaultInstance();
        Product model = null;
		try {
			RealmResults<Product> result = realm.where(Product.class).findAllSorted("updatedAt");

			if (result != null && result.size() > 0) {
				model = result.get(result.size()-1);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//realm.close();
		}
        return model;
    }
	
	public static synchronized void syncItem(final Product product) {
		Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
			realm.insertOrUpdate(product); // insere ou atualiza, sem esperar um retorno
			realm.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			realm.close();
		}
	}

	public static synchronized void syncItems(final List<Product> productList) {
		//Log.w(TAG, "List<Product>: " + productList.size());

        Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
			List<Product> pp = new ArrayList<Product>((Collection<? extends Product>) productList);
			realm.insertOrUpdate(pp);
			realm.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			realm.close();
		}
	}
	
	public static synchronized void addItem(Product product) {
		//Log.w(TAG, "SQL - Adicionar Produto");

		Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
			realm.insert(product);
			realm.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			realm.close();
		}
	}
	
	public static synchronized void addList(final List<Product> productList) {
		Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
			List<Product> pp = new ArrayList<Product>((Collection<? extends Product>) productList);
			realm.insert(pp);
			realm.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			realm.close();
		}
	}
	
	public static synchronized void removeItem(Product product) {
		//Log.w(TAG, "SQL - Remover Produto");

		Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
			product.deleteFromRealm();
			realm.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			realm.close();
		}
	}
}
