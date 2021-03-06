/*
 * Copyright 2014 Guidewire Software, Inc.
 */

package gw.internal.gosu.parser;

import com.sun.source.tree.Tree;
import gw.lang.parser.coercers.FunctionToInterfaceCoercer;
import gw.lang.parser.expressions.Variance;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.java.IJavaClassInfo;
import gw.lang.reflect.java.IJavaClassType;
import gw.lang.reflect.java.IJavaClassTypeVariable;
import gw.lang.reflect.java.IJavaClassWildcardType;
import gw.lang.reflect.java.IJavaType;
import gw.lang.reflect.java.JavaTypes;
import gw.lang.reflect.java.asm.AsmType;
import gw.lang.reflect.java.asm.AsmWildcardType;
import gw.lang.reflect.java.asm.IAsmType;
import gw.lang.reflect.module.IModule;

public class AsmWildcardTypeJavaClassWildcardType extends AsmTypeJavaClassType implements IJavaClassWildcardType {
  private final IAsmType _genType;

  public AsmWildcardTypeJavaClassWildcardType( IAsmType genType, AsmWildcardType wildcardType, IModule module ) {
    super( wildcardType, module );
    _genType = genType;
  }

  @Override
  public IJavaClassType getUpperBound() {
    // we only support one bound in Gosu

    if( maybeUseLowerBoundForFunctionalInterface() ) {
      IJavaClassType bound = AsmTypeJavaClassType.createType( ((AsmWildcardType)getType()).getBound(), getModule() );
      if( bound instanceof IJavaClassTypeVariable ) {
        ((IJavaClassTypeVariable)bound).setVariance( Variance.WILD_CONTRAVARIANT );
      }
      return bound;
    }

    AsmType asmBound = ((AsmWildcardType)getType()).getBound();
    if( asmBound == null ) {
      return JavaTypes.OBJECT().getBackingClassInfo();
    }
    IJavaClassType bound = AsmTypeJavaClassType.createType( asmBound, _module );
    if( bound instanceof IJavaClassTypeVariable ) {
      ((IJavaClassTypeVariable)bound).setVariance( Variance.WILD_COVARIANT );
    }
    return bound;
  }

  private boolean maybeUseLowerBoundForFunctionalInterface()
  {
    if( !((AsmWildcardType)getType()).isCovariant() ) {
      if( FunctionToInterfaceCoercer.getSingleMethodFromJavaInterface( (IJavaType)TypeSystem.getByFullNameIfValid( _genType.getName(), getModule() ) ) != null ) {
        // Functional interfaces parameterized with ? super T wildcard type keep T so contravariance works with blocks
        return true;
      }
    }
    return false;
  }

  @Override
  public IJavaClassType getConcreteType() {
    return getUpperBound();
  }

  @Override
  public String getSimpleName() {
    return getType().getSimpleName();
  }

  @Override
  public IModule getModule() {
    return _module;
  }

  @Override
  public Tree getTree()
  {
    return null;
  }

  @Override
  public IJavaClassInfo getEnclosingClass()
  {
    return null;
  }
}
