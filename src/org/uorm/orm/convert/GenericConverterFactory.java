/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uorm.orm.convert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训长</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-12-5       郭训长            创建<br/>
 */
public class GenericConverterFactory {

	private static final GenericConverterFactory _INSTANCE = new GenericConverterFactory();
	
	private final List<IGenericConverter> _converters;
    private final List<IReverseGenericConverter> _reverseDiscoveryConverters;
    private final Map<Key, ITypeConverter> _cache;
    private static final ITypeConverter _NULL = new ITypeConverter() {
        public Object convert(Object source, Class<?> targetType) {
            return null;
        }
    };
    
	private GenericConverterFactory() {
        _cache = new ConcurrentHashMap<Key, ITypeConverter>(16);
        _converters = new ArrayList<IGenericConverter>(2);
        registerConverter(new SqlConverter());
        registerConverter(new BaseConverter());
    	_reverseDiscoveryConverters = new ArrayList<IReverseGenericConverter>(1);
        registerReverseDiscoveryConverter(new ReflectionReverseGenericConverter());
    }
	
    public static GenericConverterFactory getInstance() {
        return _INSTANCE;
    }
    
    public IGenericConverter getSqlConverter() {
    	return _converters.get(0);
    }
    
    /**
     * Registers a converter. Registering a new converter causes the internal
     * cache to be cleared.
     */
    public void registerConverter(IGenericConverter converter) {
        _converters.add(converter);
        _cache.clear();
    }
    
    /**
     * Registers a "reverse discovery" converter. Registering a new converter
     * causes the internal cache to be cleared.
     */
    public void registerReverseDiscoveryConverter(IReverseGenericConverter converter) {
        _reverseDiscoveryConverters.add(converter);
        _cache.clear();
    }
    
    public boolean needConvert(Class<?> sourceType, Class<?> targetType) {
    	return !(targetType.isAssignableFrom(sourceType));
    }
    /**
     * Gets a converter that is capable of converting from the given sourceType
     * to the given targetType. This method first searches to see if any of the
     * registered converters are capable of making this conversion alone. If one
     * is found, it is returned. Otherwise, this method will search to see if
     * some combination of registered converters can be used to perform this
     * conversion. If so, a composite converter will be returned.
     * <P>
     * The returned converter (or lack thereof) is cached so that subsequent
     * requests for the same source and target types will be fast.
     * 
     * @return null if there is no such converter.
     */
    public ITypeConverter getConverter(Class<?> sourceType, Class<?> targetType) {
        Key key = new Key(sourceType, targetType);
        // check the cache first:
        Object cached = _cache.get(key);
        if(cached != null) {
            return cached == _NULL ? null : (ITypeConverter) cached;
        }

        // we are going to start searching to see if some chain of converters
        // can be used to perform this conversion.
        // initial node in chain:
        Node start = new Node(null, null, sourceType);
        LinkedList<Node> sourcesToBeSearched = new LinkedList<Node>();
        sourcesToBeSearched.add(start);
        // cache to store all the classes we've tested already. This is to
        // avoid our chains from looping indefinitely:
        Set<Class<?>> cache = new HashSet<Class<?>>(16);

        // Try to find a converter chain without "reverse discovery" type
        // converetrs.
        // Our particular implementation of the "reverse discovery" type
        // converter uses Java Reflection,
        // so it is better to use a chain of normal generic converters
        ITypeConverter converter = _findConverter(sourcesToBeSearched, targetType, cache, false);

        // If we failed, try to use "reverse discovery" converters
        if(converter == null && _reverseDiscoveryConverters.size() > 0) {
            // We will use a "reverse discovery" type converter only if
            // target-to-source conversion
            // is available without the use of "reverse discovery" type
            // converters. This is done to ensure
            // we do not find a converter dynamically for the target type when
            // the source type is
            // not convertable from the target type (converter's availability
            // has to be 'reflective').
            // Regular generic converters are pre-defined, so they normally do
            // not have this problem.

        	ITypeConverter reverseConv = null;
            reverseConv = _cache.get(new Key(targetType, sourceType));
            if(reverseConv == null) {
                cache.clear();
                sourcesToBeSearched.add(new Node(null, null, targetType));
                reverseConv = _findConverter(sourcesToBeSearched, sourceType, cache, false);
            }

            if(reverseConv != null) {
                cache.clear();
                sourcesToBeSearched.clear();
                sourcesToBeSearched.add(start);
                converter = _findConverter(sourcesToBeSearched, targetType, cache, true);
            }
        }

        if(converter == null) {
            // cache the fact that no such converter exists:
            _cache.put(key, _NULL);
        } else {
            _cache.put(key, converter);
        }

        return converter;
    }
    
    /**
     * tries to find a converter, or create a chain of converters that can
     * convert from one of the given sourceTypes to the specified targetType.
     * 
     * @param sourcesToBeSearched each element is a Node. Each Node is a pairing
     *        of sourceType and the chain of converters needed to produce this
     *        sourceType.
     * @param targetType the type that is needed
     * @param cache used to record which classes we've searched already.
     * @param useRevserseDiscovery true if "reverse discovery" converters should
     *        be used, false otherwise
     * @return null if no converter was found.
     */
    private ITypeConverter _findConverter(LinkedList<Node> sourcesToBeSearched, Class<?> targetType,
        Set<Class<?>> cache, boolean useRevserseDiscovery) {
        while(!sourcesToBeSearched.isEmpty()) {
            Node source = sourcesToBeSearched.removeFirst();
            ITypeConverter match = null;

            // loop through all the converters and see what types they can turn
            // the current sourceType into
            // (the current sourceType is source.targetType):
            for(IGenericConverter conv : _converters) {
                // loop though all the targetTypes on this converter to see
                // if we can find the one we're looking for:
                if(_searchTargetTypes(sourcesToBeSearched, source, conv, targetType, cache)) {
                    match = conv;
                }
            }

            if(match == null && useRevserseDiscovery)
                match = _searchSourceTypes(source.targetType, targetType);

            if(match != null) {
                // see if there is no chain:
                if(source.previous == null)
                    return match;

                // there is a chain:
                return new CompositeConverter(source, match, targetType);
            }
        }
        return null;
    }
    
    /**
     * Searches the targetTypes of the given converter to see if we can find the
     * type we are searching for.
     * 
     * @param sourcesToBeSearched each element is a Node. Each Node is a pairing
     *        of sourceType and the chain of converters needed to produce this
     *        sourceType.
     * @param currentSource a chain of converters has been used to produce the
     *        type identified by this Node. The targetType of this Node will be
     *        used to search the currentConverter.
     * @param searchType the targetType we are searching for.
     * @param cache used to record which classes we've searched already.
     * @return true if the currentConverter can convert from
     *         currentSource.targetType into searchType.
     */
    private boolean _searchTargetTypes(List<Node> sourcesToBeSearched, Node currentSource,
        IGenericConverter currentConverter, Class<?> searchType, Set<Class<?>> cache) {
        Class<?> sourceType = currentSource.targetType;
        List<Class<?>> targetTypes = currentConverter.getTargetTypes(sourceType);
        for(int i = 0, sz = targetTypes.size(); i < sz; i++) {
            Class<?> targetType = targetTypes.get(i);
            // check to see if we've seen this targetType before:
            if(cache.add(targetType)) {
                // check to see if the targetType is a subclass of the
                // searchType:
                if(searchType.isAssignableFrom(targetType))
                    return true;

                // create a new node in the chain by adding this targetType and
                // converter
                Node newSource = new Node(currentSource, currentConverter, targetType);

                // add the new node so that we can continue searching by seeing
                // if
                // we can convert the targetType into the searchType using some
                // other
                // converter:
                sourcesToBeSearched.add(newSource);
            }
        }
        return false;
    }
    
    /**
     * Finds a suitable converter by searching source types supported for a
     * given target type
     * 
     * @param sourceType - source type
     * @param targetType - target type
     * @return a suitable TypeConverter is found, null otherwise
     */
    private ITypeConverter _searchSourceTypes(Class<?> sourceType, Class<?> targetType) {
        for(IReverseGenericConverter conv: _reverseDiscoveryConverters) {
            List<Class<?>> sourceTypes = conv.getSourceTypes(targetType);
            for(Class<?> type: sourceTypes) {
                if(type.isAssignableFrom(sourceType))
                    return conv;
            }
        }
        return null;
    }
    
    private static final class Node {

        public Node(Node previous, ITypeConverter converter, Class<?> targetType) {
            this.previous = previous;
            this.converter = converter;
            this.targetType = targetType;
        }

        public Object convert(Object source) throws TypeConvertException {
            if(previous != null) {
                source = previous.convert(source);
                source = converter.convert(source, targetType);
            }
            return source;
        }

        public final Node previous;
        public final ITypeConverter converter;
        public final Class<?> targetType;
    }
    
    private static final class Key {
        private final int _hc;
        private final Class<?> _source;
        private final Class<?> _target;

        public Key(Class<?> source, Class<?> target) {
            assert !source.equals(target);
            _source = source;
            _target = target;
            _hc = source.hashCode() + target.hashCode();
        }

        @Override
        public int hashCode() {
            return _hc;
        }

        @Override
        public boolean equals(Object other) {
            if(this == other)
                return true;
            if(other instanceof Key) {
                Key that = (Key) other;
                return _source.equals(that._source) && _target.equals(that._target);
            }
            return false;
        }
    }
    
    private static final class CompositeConverter implements ITypeConverter {

        public CompositeConverter(Node source, ITypeConverter conv, Class<?> targetType) {
            assert source != null;
            _chain = new Node(source, conv, targetType);
        }

        public Object convert(Object source, Class<?> targetType) throws TypeConvertException {
            if(targetType.isAssignableFrom(_chain.targetType)) {
                return _chain.convert(source);
            } else
                throw new IllegalArgumentException("CANNOT_CONVERT");
        }

        private final Node _chain;
    }
}
