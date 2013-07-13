package eu.nazgee.simple;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IShape;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class BodyDragger implements IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private PhysicsWorld mPhysicsWorld;
	private MouseJoint mMouseJointActive;
	private Body mGroundBody;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setPhysicsWorld(PhysicsWorld pPhysicsWorld) {
		Debug.w("setPhysicsWorld @" + pPhysicsWorld);

		this.mPhysicsWorld = pPhysicsWorld;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					return true;
				case TouchEvent.ACTION_MOVE:
					if(this.mMouseJointActive != null) {
						final Vector2 vec = Vector2Pool.obtain(
								pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
								pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
						this.mMouseJointActive.setTarget(vec);
						Vector2Pool.recycle(vec);
					}
					return true;
				case TouchEvent.ACTION_UP:
					if(this.mMouseJointActive != null) {
						Debug.w("ACTION_UP");
						destroyMouseJoint(this.mPhysicsWorld);
					}
					return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final IEntity entity = (IEntity) pTouchArea;
			/*
			 * If we have a active MouseJoint, we are just moving it around
			 * instead of creating a second one.
			 */
			if(this.mMouseJointActive == null && entity instanceof IShape) {
				this.mMouseJointActive = this.createMouseJoint((IShape) entity, pTouchAreaLocalX, pTouchAreaLocalY, this.mPhysicsWorld);
			}
			return true;
		}
		return false;
	}
	// ===========================================================
	// Methods
	// ===========================================================
	public void cleanup() {
		cleanup(this.mPhysicsWorld);
	}
	public void cleanup(PhysicsWorld physicsWorld) {
		if (physicsWorld != null) {
			destroyMouseJoint(physicsWorld);
			destroyGroundBody(physicsWorld);
		}
	}
	private Body createGroundBody(PhysicsWorld physicsWorld) {
		Body body = physicsWorld.createBody(new BodyDef());
		Debug.w("createGroundBody body #" + body + " world #" + physicsWorld);
		return body;
	}
	public MouseJoint createMouseJoint(final IShape pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY, PhysicsWorld physicsWorld) {
		PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(pFace);
		if (physicsConnector != null) {
			final Body body = physicsConnector.getBody();
			final MouseJointDef mouseJointDef = new MouseJointDef();

			final Vector2 localPoint = Vector2Pool.obtain(
					(pTouchAreaLocalX - pFace.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
					(pTouchAreaLocalY - pFace.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			if (this.mGroundBody == null) {
				this.mGroundBody = createGroundBody(physicsWorld);
			}
			this.mGroundBody.setTransform(localPoint, 0);

			mouseJointDef.bodyA = this.mGroundBody;
			mouseJointDef.bodyB = body;
			mouseJointDef.dampingRatio = 0.98f;
			mouseJointDef.frequencyHz = 30;
			mouseJointDef.maxForce = (1000.0f * body.getMass());
			mouseJointDef.collideConnected = false;

			mouseJointDef.target.set(body.getWorldPoint(localPoint));
			Vector2Pool.recycle(localPoint);

			MouseJoint mousejoint = (MouseJoint) physicsWorld.createJoint(mouseJointDef);
			Debug.w("createMouseJoint joint #" + mousejoint + " world #" + physicsWorld);
			return mousejoint;
		} else {
			return null;
		}
	}
	private void destroyGroundBody(PhysicsWorld physicsWorld) {
		if (this.mGroundBody != null) {
			Debug.w("destroyGroundBody body #" + this.mGroundBody + " world #" + this.mPhysicsWorld);
			physicsWorld.destroyBody(this.mGroundBody);
			this.mGroundBody = null;
		}
	}
	private void destroyMouseJoint(PhysicsWorld physicsWorld) {
		if (this.mMouseJointActive != null) {
			Debug.w("destroyMouseJoint joint #" + mMouseJointActive + " world #" + this.mPhysicsWorld);
			physicsWorld.destroyJoint(this.mMouseJointActive);
			this.mMouseJointActive = null;
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}