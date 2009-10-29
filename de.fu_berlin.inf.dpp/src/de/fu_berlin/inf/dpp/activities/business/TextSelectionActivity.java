/*
 * DPP - Serious Distributed Pair Programming
 * (c) Freie Universitaet Berlin - Fachbereich Mathematik und Informatik - 2006
 * (c) Riad Djemili - 2006
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.fu_berlin.inf.dpp.activities.business;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import de.fu_berlin.inf.dpp.activities.serializable.IActivityDataObject;
import de.fu_berlin.inf.dpp.activities.serializable.TextSelectionActivityDataObject;
import de.fu_berlin.inf.dpp.net.JID;

@XStreamAlias("textSelectionActivity")
public class TextSelectionActivity extends AbstractActivity {

    @XStreamAsAttribute
    private final int offset;

    @XStreamAsAttribute
    private final int length;

    @XStreamAsAttribute
    private final IPath path;

    public TextSelectionActivity(JID source, int offset, int length, IPath path) {
        super(source);
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        this.offset = offset;
        this.length = length;
        this.path = path;
    }

    public int getLength() {
        return this.length;
    }

    public int getOffset() {
        return this.offset;
    }

    public IPath getEditor() {
        return this.path;
    }

    public ITextSelection getSelection() {
        return new TextSelection(this.offset, this.length);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + length;
        result = prime * result + offset;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof TextSelectionActivity))
            return false;

        TextSelectionActivity activity = (TextSelectionActivity) obj;
        return (this.offset == activity.offset)
            && (this.length == activity.length)
            && (ObjectUtils.equals(this.path, activity.path));
    }

    @Override
    public String toString() {
        return "TextSelectionActivity(offset:" + this.offset + ",length:"
            + this.length + ",src:" + getSource() + ",path:" + this.path + ")";
    }

    public boolean dispatch(IActivityConsumer consumer) {
        return consumer.consume(this);
    }

    public void dispatch(IActivityReceiver receiver) {
        receiver.receive(this);
    }

    public IActivityDataObject getActivityDataObject() {
        return new TextSelectionActivityDataObject(source, offset, length, path);
    }
}
