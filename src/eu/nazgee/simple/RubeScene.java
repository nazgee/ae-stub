package eu.nazgee.simple;

import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.rubeloader.SimpleLoader;
import org.andengine.extension.rubeloader.SimpleLoader.ITextureProvider;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.Resources;

public class RubeScene extends Scene {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public RubeScene(ITextureProvider pTextureProvider, Resources pResources, VertexBufferObjectManager pVertexBufferObjectManager, int pResourceID) {
		SimpleLoader loader = new SimpleLoader();
		PhysicsWorld world = loader.load(pResources, this, pTextureProvider, pVertexBufferObjectManager, pResourceID);
		registerUpdateHandler(world);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
