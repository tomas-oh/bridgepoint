//=====================================================================
//
//File:      $RCSfile: RTOMoveTests.java,v $
//Version:   $Revision: 1.13 $
//Modified:  $Date: 2013/05/10 04:49:52 $
//
// NOTE: This file was generated, but is maintained by hand.
// Generated by: UnitTestGenerator.pl
// Version:      1.13
// Matrix:       RTOMoveMatrix.txt
//
//(c) Copyright 2007-2014 by Mentor Graphics Corp. All rights reserved.
//
//=====================================================================
// Licensed under the Apache License, Version 2.0 (the "License"); you may not 
// use this file except in compliance with the License.  You may obtain a copy 
// of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   See the 
// License for the specific language governing permissions and limitations under
// the License.
//=====================================================================

package com.mentor.nucleus.bp.core.test.rtomove;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.Preferences;

import com.mentor.nucleus.bp.core.ComponentReference_c;
import com.mentor.nucleus.bp.core.CorePlugin;
import com.mentor.nucleus.bp.core.DataType_c;
import com.mentor.nucleus.bp.core.ExecutableProperty_c;
import com.mentor.nucleus.bp.core.ImportedReference_c;
import com.mentor.nucleus.bp.core.InterfaceReference_c;
import com.mentor.nucleus.bp.core.Interface_c;
import com.mentor.nucleus.bp.core.Ooaofooa;
import com.mentor.nucleus.bp.core.Package_c;
import com.mentor.nucleus.bp.core.PackageableElement_c;
import com.mentor.nucleus.bp.core.ProvidedExecutableProperty_c;
import com.mentor.nucleus.bp.core.ProvidedOperation_c;
import com.mentor.nucleus.bp.core.Provision_c;
import com.mentor.nucleus.bp.core.RequiredExecutableProperty_c;
import com.mentor.nucleus.bp.core.RequiredOperation_c;
import com.mentor.nucleus.bp.core.Requirement_c;
import com.mentor.nucleus.bp.core.SystemModel_c;
import com.mentor.nucleus.bp.core.UserDataType_c;
import com.mentor.nucleus.bp.core.Visibility_c;
import com.mentor.nucleus.bp.core.common.ClassQueryInterface_c;
import com.mentor.nucleus.bp.core.common.InstanceList;
import com.mentor.nucleus.bp.core.common.NonRootModelElement;
import com.mentor.nucleus.bp.core.common.Transaction;
import com.mentor.nucleus.bp.core.common.TransactionManager;
import com.mentor.nucleus.bp.core.ui.preferences.BridgePointProjectPreferences;
import com.mentor.nucleus.bp.core.util.WorkspaceUtil;
import com.mentor.nucleus.bp.test.TestUtil;
import com.mentor.nucleus.bp.test.common.BaseTest;
import com.mentor.nucleus.bp.test.common.TestingUtilities;
import com.mentor.nucleus.bp.test.common.UITestingUtilities;
import com.mentor.nucleus.bp.ui.canvas.test.CanvasTest;
import com.mentor.nucleus.bp.ui.graphics.editor.GraphicalEditor;

public class RTOMoveTests extends CanvasTest {
	public static boolean generateResults = false;
	public static boolean useDrawResults = true;

	String test_id = "";

	protected String getResultName() {
		return getClass().getSimpleName() + "_" + test_id;
	}

	protected GraphicalEditor fActiveEditor;
	private static SystemModel_c inscopeOtherProject;
	private static SystemModel_c outOfScopeOtherProject;
	private NonRootModelElement rto;
	private NonRootModelElement rgo;
	private boolean pasteSuccessful;
	private boolean cutSuccessful;
	private boolean rgoUpdateSuccessful;

	protected GraphicalEditor getActiveEditor() {
		return fActiveEditor;
	}

	public RTOMoveTests(String subTypeClassName, String subTypeArg0) {
		super(subTypeClassName, subTypeArg0);
	}

	protected String getTestId(String src, String dest, String count) {
		return "test_" + count;
	}

	@Override
	protected void initialSetup() throws Exception {
		// disable auto build
		WorkspaceUtil.setAutobuilding(false);

		TestingUtilities.importTestingProjectIntoWorkspace("RTOMoveTests");
		
		IProject testProject = getProjectHandle("RTOMoveTests");
		m_sys = getSystemModel(testProject.getName());
		m_sys.setUseglobals(true);
		m_sys.getPersistableComponent().loadComponentAndChildren(new NullProgressMonitor());

		IScopeContext projectScope = new ProjectScope(testProject);
		Preferences projectNode = projectScope
				.getNode(BridgePointProjectPreferences.BP_PROJECT_PREFERENCES_ID);
		projectNode.putBoolean(
				BridgePointProjectPreferences.BP_PROJECT_REFERENCES_ID, true);
		
		IProject inscope = TestingUtilities.createProject("InScope Other");
		inscopeOtherProject = getSystemModel(inscope.getName());
		inscopeOtherProject.setUseglobals(true);
		projectScope = new ProjectScope(inscope);
		projectNode = projectScope
				.getNode(BridgePointProjectPreferences.BP_PROJECT_PREFERENCES_ID);
		projectNode.putBoolean(
				BridgePointProjectPreferences.BP_PROJECT_REFERENCES_ID, true);
		inscopeOtherProject.Newpackage();
		IProject outOfScope = TestingUtilities
				.createProject("OutOfScope Other");
		outOfScopeOtherProject = getSystemModel(outOfScope.getName());
		outOfScopeOtherProject.Newpackage();
	}

	protected void setUp() throws Exception {
		Ooaofooa.setPersistEnabled(true);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// undo paste
		if(pasteSuccessful) {
			TransactionManager.getSingleton().getUndoAction().run();
		}
		// undo RGO update
		if(rgoUpdateSuccessful) {
			TransactionManager.getSingleton().getUndoAction().run();
		}
		// undo cut
		if(cutSuccessful) {
			TransactionManager.getSingleton().getUndoAction().run();
		}
		rgoUpdateSuccessful = false;
		super.tearDown();
	}

	/**
	 * "AC" is one of the degrees of freedom as specified in this issues test
	 * matrix. This routine gets the "AC" instance from the given name.
	 * 
	 * @param element
	 *            The degree of freedom instance to retrieve
	 * @return A model element used in the test as specified by the test matrix
	 */
	NonRootModelElement selectAC(String element) {
		String key = stripKey(element);
		Class<?> elementType = ElementMap.getElementType(key);
		rto = getElement(key, elementType, false);
		return null;
	}

	private NonRootModelElement getElement(String key, Class<?> elementType,
			boolean nonDefault) {
		NonRootModelElement element = null;
		String name = elementType.getSimpleName();
		if (key.startsWith("A")) {
			name = name + "_RTO";
		} else {
			name = name + "_RGO";
		}
		if(elementType == ImportedReference_c.class) {
			// name will match RTO
			name = "InterfaceReference_c_RTO";
		}
		if(elementType == InterfaceReference_c.class && rto instanceof Interface_c) {
			// name will match RTO
			name = "Interface_c_RTO";
		}
		if(elementType == ComponentReference_c.class) {
			// name will match RTO
			name = "Component_c_RTO";
		}
		if(elementType == ProvidedExecutableProperty_c.class) {
			// need to locate the Provided Operation
			elementType = ProvidedOperation_c.class;
			// also the name will be ExecutableProperty_c_RTO
			name = "ExecutableProperty_c_RTO";
		}
		if(elementType == RequiredExecutableProperty_c.class) {
			// need to locate the Provided Operation
			elementType = RequiredOperation_c.class;
			// also the name will be ExecutableProperty_c_RTO
			name = "ExecutableProperty_c_RTO";
		}
		if (nonDefault) {
			name = name + "_ND";
		}
		Ooaofooa[] instances = Ooaofooa.getInstances();
		for (Ooaofooa ooa : instances) {
			InstanceList list = ooa.getInstanceList(elementType);
			for (Object elem : list) {
				NonRootModelElement test = (NonRootModelElement) elem;
				if(test.isProxy()) {
					continue;
				}
				if (test.getName().contains(name) && (!nonDefault && !test.getName().contains("ND"))) {
					element = test;
					break;
				}
				if (test.getName().contains(name) && (nonDefault && test.getName().contains("ND"))) {
					element = test;
					break;
				}
			}
			if (element != null) {
				break;
			}
		}
		if (elementType == ProvidedOperation_c.class) {
			return ProvidedExecutableProperty_c
					.getOneSPR_PEPOnR4503((ProvidedOperation_c) element);
		}
		if (elementType == RequiredOperation_c.class) {
			return RequiredExecutableProperty_c
					.getOneSPR_REPOnR4502((RequiredOperation_c) element);
		}
		assertNotNull("Missing test element with details : Type->"
				+ elementType + " Name->" + name, element);
		return element;
	}

	private String stripKey(String element) {
		if (element.startsWith("A")) {
			return element.replaceAll("C.*", "");
		} else {
			return element.replaceAll("D.*", "");
		}
	}

	/**
	 * "BD" is one of the degrees of freedom as specified in this issues test
	 * matrix. This routine gets the "BD" instance from the given name.
	 * 
	 * @param element
	 *            The degree of freedom instance to retrieve
	 * @return A model element used in the test as specified by the test matrix
	 */
	NonRootModelElement selectBD(String element) {
		String key = stripKey(element);
		Class<?> elementType = ElementMap.getElementType(key);
		rgo = getElement(key, elementType, false);
		return null;
	}

	/**
	 * This routine performs the action associated with a matrix cell. The
	 * parameters represent model instances aquired based on the specifed column
	 * instance and row instance.
	 * 
	 * @param columnInstance
	 *            Model instance from the column
	 * @param rowInstance
	 *            Model instance from the row
	 */
	void AC_BD_Action(NonRootModelElement source, NonRootModelElement target) {
		cutSuccessful = false;
		pasteSuccessful = false;
		if(getName().contains("C4")) {
			// do not allow inter-project referencing
			IScopeContext projectScope = new ProjectScope(getProjectHandle(m_sys.getName()));
			Preferences projectNode = projectScope
					.getNode(BridgePointProjectPreferences.BP_PROJECT_PREFERENCES_ID);
			projectNode.putBoolean(
					BridgePointProjectPreferences.BP_PROJECT_REFERENCES_ID, false);
		} else {
			// allow inter-project referencing
			IScopeContext projectScope = new ProjectScope(getProjectHandle(m_sys.getName()));
			Preferences projectNode = projectScope
					.getNode(BridgePointProjectPreferences.BP_PROJECT_PREFERENCES_ID);
			projectNode.putBoolean(
					BridgePointProjectPreferences.BP_PROJECT_REFERENCES_ID, true);			
		}
		NonRootModelElement destination = getDestination(getSelectableElement(rto));
		// cut the source
		if(getName().contains("A1")) {
			TestUtil.okToDialog(500, false);
		}
		UITestingUtilities.cutElementsInExplorer(getCuttableElements(rto), getExplorerView());
		cutSuccessful = true;
		assertRGOReference(rgo, rto, true);
		if (rgoShouldReferToNonDefault()) {
			// set the RTO of this element to something other than default
			configureRGOReference(rgo, rto);
		}
		if(getName().contains("C3") || getName().contains("C4")) {
			// need to click Proceed on dialog that is display
			TestUtil.selectButtonInDialog(500, "Proceed", false);
		}
		UITestingUtilities.pasteClipboardContentsInExplorer(destination);
		pasteSuccessful = true;
		rto = getElement(getName().replaceAll("test", ""), rto.getClass(),
				false);
	}

	private Object[] getCuttableElements(NonRootModelElement rto) {
		List<Object> elements = new ArrayList<Object>();
		if(rto instanceof InterfaceReference_c) {
			// need to include self and the parent component
			elements.add(rto.getFirstParentComponent());
			// and add the provision or requirement
			Provision_c pro = Provision_c.getOneC_POnR4009((InterfaceReference_c) rto);
			Requirement_c req = Requirement_c.getOneC_ROnR4009((InterfaceReference_c) rto);
			if(pro != null) {
				elements.add(pro);
			} else {
				elements.add(req);
			}
		} else if(rto instanceof ExecutableProperty_c) {
			// need to cut the interface
			Interface_c iface = Interface_c.getOneC_IOnR4003((ExecutableProperty_c) rto);
			elements.add(iface);
		} else {
			elements.add(getSelectableElement(rto));
		}
		return elements.toArray();
	}

	private NonRootModelElement getSelectableElement(NonRootModelElement element) {
		if(element instanceof DataType_c) {
			return UserDataType_c.getOneS_UDTOnR17((DataType_c) element);
		}
		return element;
	}

	private NonRootModelElement getDestination(NonRootModelElement rto) {
		if(rto instanceof InterfaceReference_c && getName().contains("C1")) {
			// need the package for the parent component
			return rto.getFirstParentComponent().getFirstParentPackage();
		}
		if (getName().contains("C1")) {
			return rto.getFirstParentPackage();
		} else if (getName().contains("C2")) {
			return Package_c.getOneEP_PKGOnR1401(inscopeOtherProject);
		} else if (getName().contains("C3")) {
			return Package_c.getOneEP_PKGOnR1401(m_sys,
					new ClassQueryInterface_c() {

						@Override
						public boolean evaluate(Object candidate) {
							Package_c pkg = (Package_c) candidate;
							PackageableElement_c pe = PackageableElement_c
									.getOnePE_PEOnR8001(pkg);
							if (pe.getVisibility() == Visibility_c.Private) {
								return true;
							}
							return false;
						}
					});
		} else {
			return Package_c.getOneEP_PKGOnR1401(outOfScopeOtherProject);
		}
	}

	private void configureRGOReference(NonRootModelElement rgo,
			NonRootModelElement rto) {
		TransactionManager manager = TransactionManager.getSingleton();
		Transaction transaction = null;
		String association = ElementMap.getAssociationFor(getName().replaceAll(
				"test", ""));
		NonRootModelElement newRto = getElement(getName().replaceAll("test",
				""), rto.getClass(), true);
		try {
			transaction = manager.startTransaction("Adjust RGO", new Ooaofooa[] {Ooaofooa.getDefaultInstance()});
			if(rgo instanceof ProvidedExecutableProperty_c) {
				// need to reassign the provision
				Interface_c iface = Interface_c.getOneC_IOnR4003((ExecutableProperty_c) newRto);
				Provision_c provision = Provision_c.getOneC_POnR4501((ProvidedExecutableProperty_c) rgo);
				provision.Unformalize(false);
				provision.Formalize(iface.getId(), false);
				this.rgo = ProvidedExecutableProperty_c.getOneSPR_PEPOnR4501(provision);
			} else if(rgo instanceof RequiredExecutableProperty_c) {
				// need to reassign the requirement
				Interface_c iface = Interface_c.getOneC_IOnR4003((ExecutableProperty_c) newRto);
				Requirement_c requirement = Requirement_c.getOneC_ROnR4500((RequiredExecutableProperty_c) rgo);
				requirement.Unformalize(false);
				requirement.Formalize(iface.getId(), false);
				// need to reset the RGO
				this.rgo = RequiredExecutableProperty_c.getOneSPR_REPOnR4500(requirement);
			} else {
				Method getMethod = getAccessorMethod(rgo, rto);
				NonRootModelElement existing = (NonRootModelElement) getMethod.invoke(rto, new Object[] {rgo});
				Method method = getUnrelateMethod(association, existing, rgo);
				method.invoke(existing, new Object[] { rgo });
				method = getRelateMethod(association, newRto, rgo);
				method.invoke(newRto, new Object[] { rgo });
			}
		} catch (Exception e) {
			fail(e.getMessage());
			if(transaction != null) {
				manager.cancelTransaction(transaction);
			}
		}  finally {
			if(transaction != null) {
				manager.endTransaction(transaction);
				rgoUpdateSuccessful = true;
			}
		}
		BaseTest.dispatchEvents(0);
	}

	private Method getRelateMethod(String association, NonRootModelElement rto,
			NonRootModelElement rgo) throws SecurityException,
			NoSuchMethodException {
		return rto.getClass().getMethod(
				"relateAcrossR" + association + "To",
				new Class<?>[] { rgo.getClass() });
	}

	private Method getUnrelateMethod(String association,
			NonRootModelElement rto, NonRootModelElement rgo)
			throws SecurityException, NoSuchMethodException {
		return rto.getClass().getMethod(
				"unrelateAcrossR" + association + "From",
				new Class<?>[] { rgo.getClass() });
	}

	private boolean rgoShouldReferToNonDefault() {
		return getName().contains("D2");
	}

	private void assertRGOReference(NonRootModelElement rgo,
			NonRootModelElement rto, boolean defaultReference) {
		Method defaultMethod;
		try {
			defaultMethod = getDefaultReferenceMethod(rgo, rto);
			Object result = defaultMethod.invoke(rgo, new Object[0]);
			if (defaultReference) {
				assertTrue("After a cut the RGO of type "
						+ rgo.getClass().getSimpleName()
						+ " was not referring to default RTO.",
						((Boolean) result).booleanValue());
			} else {
				assertFalse("After a paste the RGO of type "
						+ rgo.getClass().getSimpleName()
						+ " was referring to default RTO.", ((Boolean) result)
						.booleanValue());
			}
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}

	private Method getDefaultReferenceMethod(NonRootModelElement rgo,
			NonRootModelElement rto) throws SecurityException,
			NoSuchMethodException {
		return rgo.getClass().getMethod(
				"Isreferringtodefault"
						+ rto.getClass().getSimpleName().replaceAll("_c", "")
								.toLowerCase(), new Class<?>[0]);
	}

	/**
	 * This function verifies an expected result.
	 * 
	 * @param source
	 *            A model element instance aquired through a action taken on a
	 *            column of the matrix.
	 * @param destination
	 *            A model element instance aquired through a action taken taken
	 *            on a row of the matrix.
	 * @return true if the test succeeds, false if it fails
	 */
	boolean checkResult_rgoResolvedChanged(NonRootModelElement source,
			NonRootModelElement target) {
		Method getMethod = getAccessorMethod(rgo, rto);
		try {
			Object referredTo = getMethod.invoke(rto, new Object[] {rgo});
			return referredTo == rto;
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
		return false;
	}

	private Method getAccessorMethod(NonRootModelElement rgo,
			NonRootModelElement rto) {
		String association = ElementMap.getAssociationFor(getName().replaceAll(
				"test", ""));
		Method[] methods = rto.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().matches("getOne.*OnR" + association)) {
				if (method.getParameterTypes().length == 1
						&& method.getParameterTypes()[0].isInstance(rgo)) {
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * This function verifies an expected result.
	 * 
	 * @param source
	 *            A model element instance aquired through a action taken on a
	 *            column of the matrix.
	 * @param destination
	 *            A model element instance aquired through a action taken taken
	 *            on a row of the matrix.
	 * @return true if the test succeeds, false if it fails
	 */
	boolean checkResult_rgoUnresolved(NonRootModelElement source,
			NonRootModelElement target) {
		try {
			Method defaultReferenceMethod = getDefaultReferenceMethod(rgo, rto);
			Boolean bool = (Boolean) defaultReferenceMethod.invoke(rgo,
					new Object[0]);
			return bool.booleanValue();
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
		return false;
	}

	/**
	 * This function verifies an expected result.
	 * 
	 * @param source
	 *            A model element instance aquired through a action taken on a
	 *            column of the matrix.
	 * @param destination
	 *            A model element instance aquired through a action taken taken
	 *            on a row of the matrix.
	 * @return true if the test succeeds, false if it fails
	 */
	boolean checkResult_rgoResolvedNotChanged(NonRootModelElement source,
			NonRootModelElement target) {
		Method getMethod = getAccessorMethod(rgo, rto);
		try {
			Object referredTo = getMethod.invoke(rto, new Object[] {rgo});
			Method defaultReferenceMethod = getDefaultReferenceMethod(rgo, rto);
			Boolean bool = (Boolean) defaultReferenceMethod.invoke(rgo,
					new Object[0]);
			return referredTo != rto && !bool.booleanValue();
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		}
		return false;
	}

}
