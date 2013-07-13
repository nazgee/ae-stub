package eu.nazgee.simple;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.rubeloader.ITextureProvider;
import org.andengine.extension.rubeloader.RubeLoader;
import org.andengine.extension.rubeloader.def.RubeDef;
import org.andengine.extension.rubeloader.factory.EntityFactory;
import org.andengine.extension.rubeloader.factory.IEntityFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.Resources;

import com.badlogic.gdx.physics.box2d.Body;

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
		/**
		 * Extend EntityFactory if you need more fine-grained control over creation of entities
		 * that are to be connected to bodies. Basic EntityFactory creates UncoloredSprite for
		 * every Entity (which you might not like).
		 *
		 * EntityFactory::produce() might be a good place to create textured polygons if you need them.
		 */
		IEntityFactory entityFactory = new EntityFactory(this, pTextureProvider, pVertexBufferObjectManager);

		/**
		 * Loader is an object that will build RUBE worlds for you. If you are using
		 * custom IEntityFactory pass it via Loader's constructors. If you do not have your
		 * custom IEntityFactory use a default Loader() constructor (default EntityFactory will
		 * be created under the hood and used by Loader).
		 * 
		 * Under the hood it uses RubeParser to parser json files.
		 */
		RubeLoader loader = new RubeLoader(entityFactory);

		/**
		 * This is how resource is converted to RubeDef (i.e. world is loaded).
		 * 
		 * RubeDef is an object that allows you to query for objects/entities/fixtures and much more.
		 * 
		 * It is extremely messy so far and is pending a rewrite (e.g. creation of fance getters) but
		 * I'm not able to do it right now. Sorry.
		 */
		RubeDef rubeDef = loader.load(pResources, this, pTextureProvider, pVertexBufferObjectManager, pResourceID);

		/**
		 * Remember to register your world! otherwise it will not be updated!
		 */
		registerUpdateHandler(rubeDef.worldProvider.getWorld());

		/**
		 * This is just an example how you can use RubeDef to get into
		 * the guts of world you are working with
		 */
		Body badgeBody = rubeDef.getBodyByName("badge");
		IEntity badgeEntity = rubeDef.getImageByName("image0");

		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);
		/**
		 * This is not rube related anymore - I use this BodyDragger to manage touch events.
		 * BodyDragger creates MouseJoint and drags bodies around.
		 * 
		 * BodyDragger is triggered when ITouchArea is touched so you need to register it with registerTouchArea().
		 * When triggered it attempts to map Entity to Body, and when it is successfull, it manipulates the body
		 * by creating a MouseJoint between the body and the ground.
		 */
		registerTouchArea(badgeEntity);

		BodyDragger dragger = new BodyDragger();
		dragger.setPhysicsWorld(rubeDef.worldProvider.getWorld());

		setOnSceneTouchListener(dragger);
		setOnAreaTouchListener(dragger);
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
